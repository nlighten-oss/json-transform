import { describe, test } from "vitest";
import { assertTransformation} from "../BaseTransformationTest";

describe("TransformerFunctionCoalesce", () => {
  test("object", () => {
    const arr = [ null, null, "c"];
    assertTransformation(arr, { "$$coalesce": "$" }, arr[2]);
    assertTransformation(arr, { "$$coalesce": ["$[0]", "b", "c"] }, "b");
  });

  test("aliasFirst", () => {
    const arr = [ null, null, "c"];
    assertTransformation(arr, { "$$first": "$" }, arr[2]);
  });
});
