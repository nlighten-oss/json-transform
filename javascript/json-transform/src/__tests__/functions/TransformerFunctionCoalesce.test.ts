import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionCoalesce", () => {
  test("object", async () => {
    const arr = [null, null, "c"];
    await assertTransformation(arr, { $$coalesce: "$" }, arr[2]);
    await assertTransformation(arr, { $$coalesce: ["$[0]", "b", "c"] }, "b");
  });

  test("aliasFirst", async () => {
    const arr = [null, null, "c"];
    await assertTransformation(arr, { $$first: "$" }, arr[2]);
  });
});
