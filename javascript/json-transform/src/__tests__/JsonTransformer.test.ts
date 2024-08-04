import { describe, expect, test } from "vitest";
import JsonTransformer from "../JsonTransformer";
import { assertFailTransformation, assertTransformation } from "./BaseTransformationTest";

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
});
