import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";
import { BigDecimal } from "../../functions/common/FunctionHelpers";
import BigNumber from "bignumber.js";

describe("TransformerFunctionLong", () => {
  test("convert", async () => {
    const decimals = "123456789.87654321";
    const longVal = BigInt(new BigDecimal(decimals).toFixed(0, BigNumber.ROUND_DOWN));
    await assertTransformation(decimals, "$$long:$", longVal);
    await assertTransformation(decimals, "$$long():$", longVal);
    await assertTransformation(123456789.87654321, "$$long:$", longVal);
    await assertTransformation(null, "$$long:$", null);
  });
  test("maxLong", async () => {
    const max = BigInt("9223372036854775807"); // MAX_LONG
    await assertTransformation(max, "$$long:$", max);
  });
});
