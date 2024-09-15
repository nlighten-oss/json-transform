import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";
import { BigDecimal, DEFAULT_LOCALE } from "../../functions/common/FunctionHelpers";

describe("TransformerFunctionNumberParse", () => {
  test("inline", async () => {
    const decimals = "123456789.88";
    const val = new BigDecimal(decimals);

    await assertTransformation(decimals, "$$numberparse:$", val);
    await assertTransformation(decimals, "$$numberparse():$", val);
    // literal
    await assertTransformation("123,456,789.88", "$$numberparse('#,##0.00'):$", val);
    await assertTransformation("123.456.789,88", "$$numberparse('#,##0.00',de):$", val);
    await assertTransformation("123.456.789,88", "$$numberparse('#,##0.00',en-US,'.',','):$", val);

    // HEX
    const hex = "75bcd15";
    await assertTransformation(hex, "$$numberparse(BASE,16):$", BigDecimal("123456789"));
    const binary = "00001010";
    await assertTransformation(binary, "$$numberparse(BASE,2):$", BigDecimal("10"));
  });
});
