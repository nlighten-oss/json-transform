import { describe, expect, test } from "vitest";
import JsonTransformer from "../JsonTransformer";
import {assertFailTransformation, assertTransformation} from "./BaseTransformationTest";

describe("JsonTransformer", () => {
  test("lower", () => {
    const x = new JsonTransformer("$$lower:$.x")
    expect(x.transform({ x: "HELLO" })).toEqual("hello");
  });

  test("DontCopyEscaped", () => {
    var text = "text";
    assertTransformation(text, "\\$", "$");
    assertTransformation(text, "\\#uuid", "#uuid");
    // regex matches
    assertTransformation(text, "$0", "$0");
    assertTransformation(text, "$1", "$1");
  });

  test("DontCopyUnrecognized", () => {
    assertTransformation(null, "#unknown", "#unknown");
    assertTransformation(null, "$$testunknown:#now", "$$testunknown:#now");
  });

  test("JsonPathCopy", () => {
    var val = "test";
    assertTransformation(val, "$", val);
    assertTransformation(JSON.parse("{\"a\":\"" + val + "\"}"), "$.a", val);
    assertTransformation(JSON.parse("[\"" + val + "\"]"), "$[0]", val);
  });

  test("JsonPathCopyInteger", () => {
    var val = 123;
    assertTransformation(val, "$", val);
  });

  test("JsonPathCopyBoolean", () => {
    var val = true;
    assertTransformation(val, "$", val);
  });

  test("JsonPathCopyString", () => {
    var text = "text";
    assertTransformation(text, "$", text);
  });

  test("JsonPathCopyNull", () => {
    const val = null;
    assertTransformation(val, "$", val);
  });

  test("JsonPathCopyFromAdditionalRoot", () => {
    var additionalContext = {
      $extra: { y: "text" }
    };
    assertTransformation(null, "$extra.y", additionalContext.$extra.y, additionalContext);

    // array
    var additionalContext2 = {
      $extra: [1, 2]
    };
    assertTransformation(null, "$extra[0]", additionalContext2.$extra[0], additionalContext2);

    // unrecognized root
    assertTransformation(null, "$extra2.y", "$extra2.y", additionalContext);
  });

  test("MacroUUID", () => {
    var result = new JsonTransformer("#uuid").transform();
    expect(result).toHaveLength(36);
  });

  test("MacroNow", () => {
    var result = new JsonTransformer("#now").transform();
    expect(isNaN(new Date(result).getDate())).not.toBeTruthy();
  });

  test("InputExtractorSpread", () => {
    var m1 ={
      "a": "A",
      "b": "B"
    };
    var t1 = {
      "*": "$",
      "a": "AA"
    };
    var e1 = {
      "a": "AA",
      "b": "B"
    };
    assertTransformation(m1, t1, e1);

    // check bad case
    assertFailTransformation(m1, t1, t1);
  });

  test("InputExtractorSpreadRemoveByHashNull", () => {
    assertTransformation({
      "a": "A",
      "b": "B"
    }, {
      "*": "$",
      "a": "#null"
    },
    {
      "a": null,
      "b": "B"
    });
  });

  test("InputExtractorSpreadDontRemoveByNull", () => {
    assertTransformation({
      "a": "A",
      "b": "B"
    }, {
      "*": "$",
      "a": null
    }, {
      "a": "A",
      "b": "B"
    });
  });

  test("InputExtractorSpreadArray", () => {
    var m1 = {
      "X": {
        "a": "A",
          "b": "B"
      },
        "Y": {
        "c": "C",
          "d": "D"
      }
    };
    var t1 = {
      "*": [ "$.X", "$.Y" ],
      "a": "AA",
      "c": "CC"
    };
    var e1 = {
      "a": "AA",
      "b": "B",
      "c": "CC",
      "d": "D"
    };
    assertTransformation(m1, t1, e1);

    // check bad case
    assertFailTransformation(m1, t1, t1);
  });

  test("InputExtractorSpreadArray2", () => {
    var m1 = {
      "X": {
      "a": "A",
        "b": "B",
        "c": "C"
    },
      "Y": {
      "a": 1,
        "b": 2
    }
    };
    var t1 = {
      "*": [ "$.X", "$.Y" ],
      "a": true
    };
    var e1 = {
      "a": true,
      "b": 2,
      "c": "C"
    };
    assertTransformation(m1, t1, e1);

    // check bad case
    assertFailTransformation(m1, t1, t1);
  });

  test("InputExtractorTransformObjectInput", () => {
    assertTransformation({
      "x": "foo"
    }, "$", {
      "x": "foo"
    });
  });

  test("InputExtractorTransformDefinitionJsonArray", () => {
    // Given input is an object and InputExtractor definition is an array
    var definition : any[] = [];
    definition.push("element1");
    definition.push(1.23);
    definition.push(false);
    definition.push('c');
    definition.push(null);
    definition.push({
      nested: "*",
    });

    assertTransformation({
      "x": "foo"
    }, definition, definition);
  });
});