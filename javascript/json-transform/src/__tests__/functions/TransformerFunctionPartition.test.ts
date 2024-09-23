import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionPartition", () => {
  test("inline", async () => {
    await assertTransformation([1, 2, 3, 4, 5, 6, 7, 8, 9, 10], "$$partition(3):$", [
      [1, 2, 3],
      [4, 5, 6],
      [7, 8, 9],
      [10],
    ]);
  });

  test("object", async () => {
    await assertTransformation([1, 2, 3, 4, 5, 6, 7, 8, 9, 10], { $$partition: "$", size: 3 }, [
      [1, 2, 3],
      [4, 5, 6],
      [7, 8, 9],
      [10],
    ]);
  });
});
