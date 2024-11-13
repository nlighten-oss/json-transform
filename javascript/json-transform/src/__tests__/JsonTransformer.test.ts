import { describe, expect, test } from "vitest";
import JsonTransformer from "../JsonTransformer";
import { assertFailTransformation, assertTransformation } from "./BaseTransformationTest";
import transformerFunctions from "../transformerFunctions";
import TransformerFunction from "../functions/common/TransformerFunction";
import FunctionContext from "../functions/common/FunctionContext";
import { ArgType } from "../functions/common/ArgType";
import { getAsString, isNullOrUndefined } from "../JsonHelpers";
import { BigDecimal } from "../functions/common/FunctionHelpers";

describe("JsonTransformer", () => {
  test("lower", async () => {
    const x = new JsonTransformer("$$lower:$.x");
    const result = await x.transform({ x: "HELLO" });
    expect(result).toEqual("hello");
  });

  test("DontCopyEscaped", async () => {
    const text = "text";
    await assertTransformation(text, "\\$", "$");
    await assertTransformation(text, "\\#uuid", "#uuid");
    // regex matches
    await assertTransformation(text, "$0", "$0");
    await assertTransformation(text, "$1", "$1");
  });

  test("DontCopyUnrecognized", async () => {
    await assertTransformation(null, "#unknown", "#unknown");
    await assertTransformation(null, "$$testunknown:#now", "$$testunknown:#now");
  });

  test("JsonPathCopy", async () => {
    var val = "test";
    await assertTransformation(val, "$", val);
    await assertTransformation(JSON.parse('{"a":"' + val + '"}'), "$.a", val);
    await assertTransformation(JSON.parse('["' + val + '"]'), "$[0]", val);
  });

  test("JsonPathCopyInteger", async () => {
    var val = 123;
    await assertTransformation(val, "$", val);
  });

  test("JsonPathCopyBoolean", async () => {
    var val = true;
    await assertTransformation(val, "$", val);
  });

  test("JsonPathCopyString", async () => {
    var text = "text";
    await assertTransformation(text, "$", text);
  });

  test("JsonPathCopyNull", async () => {
    const val = null;
    await assertTransformation(val, "$", val);
  });

  test("JsonPathCopyFromAdditionalRoot", async () => {
    var additionalContext = {
      $extra: { y: "text" },
    };
    await assertTransformation(null, "$extra.y", additionalContext.$extra.y, additionalContext);

    // array
    var additionalContext2 = {
      $extra: [1, 2],
    };
    await assertTransformation(null, "$extra[0]", additionalContext2.$extra[0], additionalContext2);

    // unrecognized root
    await assertTransformation(null, "$extra2.y", "$extra2.y", additionalContext);
  });

  test("MacroUUID", async () => {
    var result = await new JsonTransformer("#uuid").transform();
    expect(result).toHaveLength(36);
  });

  test("MacroNow", async () => {
    var result = await new JsonTransformer("#now").transform();
    expect(isNaN(new Date(result).getDate())).not.toBeTruthy();
  });

  test("InputExtractorSpread", async () => {
    var m1 = {
      a: "A",
      b: "B",
    };
    var t1 = {
      "*": "$",
      a: "AA",
    };
    var e1 = {
      a: "AA",
      b: "B",
    };
    await assertTransformation(m1, t1, e1);

    // check bad case
    await assertFailTransformation(m1, t1, t1);
  });

  test("InputExtractorSpreadRemoveByHashNull", async () => {
    await assertTransformation(
      {
        a: "A",
        b: "B",
      },
      {
        "*": "$",
        a: "#null",
      },
      {
        a: null,
        b: "B",
      },
    );
  });

  // skipped since it doesn't work the same in javascript (null are being treated as values)
  test.skip("InputExtractorSpreadDontRemoveByNull", async () => {
    await assertTransformation(
      {
        a: "A",
        b: "B",
      },
      {
        "*": "$",
        a: null,
      },
      {
        a: "A",
        b: "B",
      },
    );
  });

  test("InputExtractorSpreadArray", async () => {
    var m1 = {
      X: {
        a: "A",
        b: "B",
      },
      Y: {
        c: "C",
        d: "D",
      },
    };
    var t1 = {
      "*": ["$.X", "$.Y"],
      a: "AA",
      c: "CC",
    };
    var e1 = {
      a: "AA",
      b: "B",
      c: "CC",
      d: "D",
    };
    await assertTransformation(m1, t1, e1);

    // check bad case
    assertFailTransformation(m1, t1, t1);
  });

  test("InputExtractorSpreadArray2", async () => {
    var m1 = {
      X: {
        a: "A",
        b: "B",
        c: "C",
      },
      Y: {
        a: 1,
        b: 2,
      },
    };
    var t1 = {
      "*": ["$.X", "$.Y"],
      a: true,
    };
    var e1 = {
      a: true,
      b: 2,
      c: "C",
    };
    await assertTransformation(m1, t1, e1);

    // check bad case
    assertFailTransformation(m1, t1, t1);
  });

  test("InputExtractorTransformObjectInput", async () => {
    await assertTransformation(
      {
        x: "foo",
      },
      "$",
      {
        x: "foo",
      },
    );
  });

  test("InputExtractorTransformDefinitionJsonArray", async () => {
    // Given input is an object and InputExtractor definition is an array
    var definition: any[] = [];
    definition.push("element1");
    definition.push(1.23);
    definition.push(false);
    definition.push("c");
    definition.push(null);
    definition.push({
      nested: "*",
    });

    await assertTransformation(
      {
        x: "foo",
      },
      definition,
      definition,
    );
  });

  test("testJsonElementStreamsImmediateEvaluation", async () => {
    let callCount = { value: 0 };
    class ImmediateEvaluationTest extends TransformerFunction {
      constructor() {
        super({});
      }

      override async apply(context: FunctionContext) {
        callCount.value++;
        return context.get(null);
      }
    }
    transformerFunctions.registerFunctions({ c: new ImmediateEvaluationTest() });

    callCount.value = 0;
    await assertTransformation(
      null,
      {
        $$find: {
          $$map: ["$$c:a", "$$c:b", "$$c:c", "$$c:d", "$$c:e"],
          to: "$$upper:##current",
        },
        by: "$$is(=,B):##current",
      },
      "B",
    );
    // assert that "c" was called twice (check A, check B, break)
    expect(callCount.value).toBe(2);

    callCount.value = 0;
    await assertTransformation(
      null,
      {
        $$is: "b",
        in: ["$$c:a", "$$c:b", "$$c:c", "$$c:d"],
      },
      true,
    );
    // assert that "c" was called twice (check a, check b, break)
    expect(callCount.value).toBe(2);

    callCount.value = 0;
    await assertTransformation(
      false,
      {
        $$and: ["$$c:$", "$$c:a", "$$c:b"],
      },
      false,
    );
    // assert that "c" was called once (check false, break)
    expect(callCount.value).toBe(1);

    callCount.value = 0;
    await assertTransformation(
      false,
      {
        $$or: ["$$c:true", "$$c:$"],
      },
      true,
    );
    // assert that "c" was called once (check true, break)
    expect(callCount.value).toBe(1);

    callCount.value = 0;
    await assertTransformation(
      false,
      {
        $$first: {
          $$map: [1, 2, 3],
          to: "$$c:##current",
        },
      },
      1,
    );
    // assert that "c" was called once (mapped first, break)
    expect(callCount.value).toBe(1);

    callCount.value = 0;
    await assertTransformation(
      null,
      {
        $$at: ["$$c:a", "$$c:b", "$$c:c", "$$c:d"],
        index: 2,
      },
      "c",
    );
    // assert that "c" was called once (skipped 2, break)
    expect(callCount.value).toBe(1);

    callCount.value = 0;
    await assertTransformation(
      null,
      {
        $$slice: ["$$c:a", "$$c:b", "$$c:c", "$$c:d"],
        begin: 1,
        end: 3,
      },
      ["b", "c"],
    );
    // assert that "c" was called twice (skipped 1, eval 2, break)
    expect(callCount.value).toBe(2);
  });

  class TransformerFunctionArgsTest extends TransformerFunction {
    constructor() {
      super({
        arguments: {
          a: { type: ArgType.Any, position: 0, defaultIsNull: true },
          b: { type: ArgType.Any, position: 1, defaultIsNull: true },
          c: { type: ArgType.Any, position: 2, defaultIsNull: true },
        },
      });
    }
    override async apply(context: FunctionContext) {
      if (!context.has("a")) return "N/A";
      const lst = [];
      lst.push(await context.get("a"));
      if (context.has("b")) {
        lst.push(await context.get("b"));
      }
      if (context.has("c")) {
        lst.push(await context.get("c"));
      }
      if (lst.length == 1) {
        return isNullOrUndefined(lst[0]) ? "[NULL]" : lst[0];
      }
      return lst.map(arg => "[" + (isNullOrUndefined(arg) ? "NULL" : getAsString(arg)) + "]").toString();
    }
  }

  test("inlineArgsParsingTest", async () => {
    transformerFunctions.registerFunctions({ argstest: new TransformerFunctionArgsTest() });
    await assertTransformation(null, "$$argstest(,):", "[],[]");

    await assertTransformation(null, "$$argstest", "N/A");
    await assertTransformation(null, "$$argstest:", "N/A");
    await assertTransformation(null, "$$argstest()", "N/A");
    await assertTransformation(null, "$$argstest():", "N/A");
    await assertTransformation(null, "$$argstest(,):", "[],[]");
    await assertTransformation(null, "$$argstest(#null):", "[NULL]");
    await assertTransformation(null, "$$argstest(null,#null):", "[null],[NULL]");
    await assertTransformation(null, "$$argstest(a):", "a");
    await assertTransformation("A", "$$argstest($):", "A");
    await assertTransformation("A", "$$argstest(\\$):", "$");
    await assertTransformation("A", "$$argstest(\\$,\\$):", "[$],[$]");
    await assertTransformation("A", "$$argstest('\\\\$'):", "$");
    await assertTransformation("A", "$$argstest('\\\\$','\\\\$'):", "[$],[$]");
    await assertTransformation("A", "$$argstest(\\\\$):", "\\\\$");
    await assertTransformation(true, "$$argstest($):", true);
    await assertTransformation(true, "$$argstest('$'):", true);
    await assertTransformation(BigDecimal(123.4), "$$argstest($):", BigDecimal(123.4));
    await assertTransformation([1, 2], "$$argstest($):", [1, 2]);
    await assertTransformation(null, "$$argstest( ):", " ");
    await assertTransformation(null, "$$argstest(' '):", " ");
    await assertTransformation(null, "$$argstest(  '  a' ):", "  a");
    await assertTransformation(null, "$$argstest('a',b):", "[a],[b]");
    await assertTransformation(null, "$$argstest('a','b'):", "[a],[b]");
    await assertTransformation(null, "$$argstest('\\'','\\''):", "['],[']");
    await assertTransformation(null, "$$argstest(  '\\'',  '\\''):", "['],[']");
    await assertTransformation(null, "$$argstest(a, b):", "[a],[ b]");
    await assertTransformation(null, "$$argstest( a):", " a");
    await assertTransformation(null, "$$argstest( a ):", " a ");
    await assertTransformation(null, "$$argstest( a, b):", "[ a],[ b]");
    await assertTransformation(null, "$$argstest( a , b):", "[ a ],[ b]");
    await assertTransformation(null, "$$argstest( a , b  , 'c  ' ):", "[ a ],[ b  ],[c  ]");
    await assertTransformation(null, "$$argstest(a,' b'):", "[a],[ b]");
    await assertTransformation(null, "$$argstest('\\n\\r\\t\\u0f0f'):", "\n\r\t\u0f0f");
    // not detected
    await assertTransformation(null, "$$argstest(\n\r\t\u0f0f)", "$$argstest(\n\r\t\u0f0f)");
  });

  class TransformerFunctionValTest extends TransformerFunction {
    constructor() {
      super({});
    }
    override async apply(context: FunctionContext) {
      const value = await context.getUnwrapped(null);
      if (value == null) return "NULL";
      return value;
    }
  }

  test("inlineValueParsingTest", async () => {
    transformerFunctions.registerFunctions({ valtest: new TransformerFunctionValTest() });

    await assertTransformation(null, "$$valtest", "NULL");
    await assertTransformation(null, "$$valtest:", "");
    await assertTransformation(null, "$$valtest:A", "A");
    await assertTransformation("IN", "$$valtest:$", "IN");
    await assertTransformation("IN", "$$valtest:\\$", "$");
    // regex replacements
    await assertTransformation("IN", "$$valtest:$1", "$1");
  });
});
