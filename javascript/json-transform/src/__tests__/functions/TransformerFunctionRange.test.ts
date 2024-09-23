import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";
import { BigDecimal } from "../../functions/common/FunctionHelpers";

describe("TransformerFunctionRange", () => {
  const B1 = BigDecimal(1),
    B2 = BigDecimal(2),
    B3 = BigDecimal(3),
    B4 = BigDecimal(4),
    B5 = BigDecimal(5),
    B10 = BigDecimal(10),
    B20 = BigDecimal(20),
    B30 = BigDecimal(30),
    B40 = BigDecimal(40),
    B50 = BigDecimal(50);
  test("inline", async () => {
    await assertTransformation(null, "$$range(1,5)", [B1, B2, B3, B4, B5]);
    await assertTransformation(null, "$$range(1,5):", [B1, B2, B3, B4, B5]);
    await assertTransformation(null, "$$range(1,5,1):", [B1, B2, B3, B4, B5]);
    await assertTransformation(new BigDecimal(2.7), "$$range(1.5,$,0.5):", [
      BigDecimal(1.5),
      BigDecimal(2.0),
      BigDecimal(2.5),
    ]);
    await assertTransformation(null, "$$range(1,5,2)", [B1, B3, B5]);
    await assertTransformation(null, "$$range(10,45, 10)", [B10, B20, B30, B40]);
    await assertTransformation({ start: 10, end: 50, step: 10 }, "$$range($.start,$.end,$.step)", [
      B10,
      B20,
      B30,
      B40,
      B50,
    ]);
    // bad inputs
    await assertTransformation(null, "$$range", []);
    await assertTransformation(null, "$$range():", []);
    await assertTransformation(null, "$$range(1):", []);
  });
});
