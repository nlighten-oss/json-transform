import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";
import { BigDecimal } from "../../functions/common/FunctionHelpers";

describe("TransformerFunctionAt", () => {
  test("inline", async () => {
    const arr = [4, 2, 13];
    await assertTransformation(arr, "$$at(0):$", 4);
    await assertTransformation(arr, "$$at(1):$", 2);
    await assertTransformation(arr, "$$at(-1):$", 13);
    await assertTransformation(arr, "$$at(3):$", null);
    await assertTransformation(arr, "$$at:$", null);
  });

  test("object", async () => {
    const arr = [4, 2, BigDecimal(13)];
    await assertTransformation(arr, { $$at: "$", index: 0 }, 4);
    await assertTransformation(arr, { $$at: "$", index: 1 }, 2);
    await assertTransformation(arr, { $$at: "$", index: -1 }, BigDecimal(13));
    await assertTransformation(arr, { $$at: "$", index: 3 }, null);
    await assertTransformation(arr, { $$at: "$" }, null);
  });
});
