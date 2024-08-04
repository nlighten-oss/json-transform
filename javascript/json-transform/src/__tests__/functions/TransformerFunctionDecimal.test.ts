import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";
import BigNumber from "bignumber.js";
import { BigDecimal } from "../../functions/common/FunctionHelpers";

describe("TransformerFunctionDecimal", () => {
  test("convert", async () => {
    const decimals = "123456789.87654321";
    const val = new BigDecimal(decimals);
    await assertTransformation(decimals, "$$decimal:$", val);
    await assertTransformation(decimals, "$$decimal():$", val);
    await assertTransformation(123456789.87654321, "$$decimal:$", val);
    await assertTransformation(null, "$$decimal:$", null);
  });

  test("scaling", async () => {
    const decimals = "123456789.87654321";
    await assertTransformation(decimals, "$$decimal(2):$", new BigDecimal("123456789.88"));
    await assertTransformation(decimals, "$$decimal(2,FLOOR):$", new BigDecimal("123456789.87"));
    const overmax = "1.01234567890123456789";
    await assertTransformation(overmax, "$$decimal:$", new BigDecimal("1.012345678901235"));
  });

  test("object", async () => {
    const decimals = "123456789.87654321";
    await assertTransformation(decimals, { $$decimal: "$", scale: 2 }, new BigDecimal("123456789.88"));
    await assertTransformation(
      decimals,
      { $$decimal: "$", scale: 2, rounding: "FLOOR" },
      new BigDecimal("123456789.87"),
    );
    const overmax = "1.01234567890123456789";
    await assertTransformation(overmax, { $$decimal: "$" }, new BigDecimal("1.012345678901235"));
  });
});
