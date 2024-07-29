import { describe, test } from "vitest";
import { assertTransformation} from "../BaseTransformationTest";

describe("TransformerFunctionCoalesce", () => {
  test("object", () => {
    const arr = [ "a", "b", "c" ];
    const arr2 = [ "d", "e", "f" ];
    const arr3 = [ arr, arr2 ];
    assertTransformation(arr3, { "$$concat": [ "$[0]", "$[1]" ] }, ["a","b","c","d","e","f"]);

    // keep nulls
    assertTransformation(arr3, { "$$concat": [ ["a",null,"c"], ["d","e",null] ]}, ["a",null,"c","d","e",null]);

    // skip nulls
    assertTransformation(arr, { "$$concat": [ ["a","b","c"], null ]}, ["a","b","c"]);

    // append non nulls
    assertTransformation(arr, { "$$concat": [ ["a","b","c"], "d", ["e"] ] }, ["a","b","c","d","e"]);

    // blind concat
    assertTransformation([ ["a","b","c"], "d", ["e"] ], { "$$concat": "$" }, ["a","b","c","d","e"]);
  });
});
