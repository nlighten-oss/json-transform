import { describe, test } from "vitest";
import { assertTransformation} from "../BaseTransformationTest";

describe("TransformerFunctionIs", () => {
  test("assertEq_NotEq", () => {
    assertTransformation("A", 
    { "$$is": "$", "eq": "A" }, true);
    assertTransformation("A", 
    { "$$is": "$", "eq": "B" }, false);
    assertTransformation(4,  
    { "$$is": "$", "eq": 4 }, true);
    assertTransformation(4.5, 
    { "$$is": "$", "eq": 4 }, false);
    assertTransformation(4.5, 
    { "$$is": "$", "neq": 4 }, true);
    assertTransformation(4.5, 
    { "$$is": "$", "eq": 4.5, "neq": 4 }, true);
  });

  test("assertGt_Gte_Lt_Lte", () => {
    assertTransformation("B", 
    { "$$is": "$", "gt": "A" }, true);
    assertTransformation("B",  
    { "$$is": "$", "gte": "B" }, true);
    assertTransformation(4, 
    { "$$is": "$", "gt": 3 }, true);
    assertTransformation(4, 
    { "$$is": "$", "gte": 4 }, true);
    assertTransformation(4, 
    { "$$is": "$", "lte": 4 }, true);
    assertTransformation(3, 
    { "$$is": "$", "lt": 4 }, true);
    assertTransformation(4, 
    { "$$is": "$", "lt": 4 }, false);
    assertTransformation([1,2,3],
    { "$$is": "$", "lt": [true,"a","b","c"] }, true);
    assertTransformation([1,2,3],
    { "$$is": "$", "gte": ["a","b","c"] }, true);
    assertTransformation(
    { "a": 1, "b": 2 },
    { "$$is": "$", "gte": { "key1": "a", "key2": "b" } },
    true);
  });

  test("assertIn_NotIn", () => {
    assertTransformation("A", 
    { "$$is": "$", "in": ["A", "B"] }, true);

    assertTransformation(["A", "B"],
    { "$$is": "A", "in": "$" }, true);
    assertTransformation(["A", "B"],
    { "$$is": "B", "in": ["$[0]","$[1]"] },
    true);
    assertTransformation(["a", "B"],
    { "$$is": "A", "in": "$" }, false);
    // other types
    assertTransformation([false, true],
    { "$$is": true, "in": "$" }, true);
    assertTransformation(null, 
    { "$$is": 30, "in": [10,20,30] }, true);
    assertTransformation(null, 
    { "$$is": 30, "nin": [10,20,30] }, false);
    assertTransformation(null, 
    { "$$is": 30, "in": "$" }, false);
    assertTransformation(null, 
    { "$$is": 30, "nin": "$" }, false);
    // even complex
    assertTransformation(null, 
    { "$$is": [{"a": 1}], "in": [[{"a": 4}], [{"a": 1}], [{"a": 3}]] },
    true);
    assertTransformation(null, 
    { "$$is": 30, "in": [10,20,30], "nin": [40,50,60] }, true);
    assertTransformation(null, 
    { "$$is": 30, "in": [40,50,60], "nin": [10,20,30] }, false);
  });

  test("andOrExample", () => {
    // check if number is between 1 < x < 3 or 4 <= x <= 6
    var between1And3 =
    { "$$is": "$", "gt": 1, "lt": 3 };
    var between4And6 =
    { "$$is": "$", "gte": 4, "lte": 6 };
    var definition = 
    { "$$is": true, "in": [ between1And3 , between4And6 ] };

    var goodValues = [2, 4, 5, 6];
    var badValues = [1, 3, 7];

    for (const value of goodValues) {
      assertTransformation(value, definition, true);
    }
    for (const value of badValues) {
      assertTransformation(value, definition, false);
    }
  });

  test("objectOpThat", () => {
    assertTransformation("A",
    { "$$is": "$", "op": "EQ", "that": "A" }, true);
    assertTransformation("A", 
    { "$$is": "$", "op": "EQ", "that": "B" }, false);
    assertTransformation("A", 
    { "$$is": "$", "op": "!=", "that": "B" }, true);
    assertTransformation(5,
    { "$$is": "$", "op": ">", "that": 2 }, true);
  });

  test("inlineOpThat", () => {
    assertTransformation("A", "$$is(EQ,A):$", true);
    assertTransformation("A", "$$is(=,B):$", false);
    assertTransformation("A", "$$is(!=,B):$", true);
    // string comparison vs number comparison
    assertTransformation("10", "$$is(>,2):$", false);
    assertTransformation(10, "$$is(>,2):$", true);
    // in / not in
    assertTransformation(
      ["a","b","A","B"], "$$is(IN,$):A", true);
    assertTransformation(
      ["a","b","A","B"], "$$is(IN,$):C", false);
    assertTransformation(
      ["a","b","A","B"], "$$is(NIN,$):C", true);
    assertTransformation(null, "$$is(in,$):C", false);
    assertTransformation(null, "$$is(Nin,$):C", false);
  });

  test("inlineCompareToNull", () => {
    assertTransformation(null, "$$is(!=,#null):$", false);
    assertTransformation(null, "$$is(=,#null):$", true);
  });
});