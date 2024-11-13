import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";
import { BigDecimal } from "../../functions/common/FunctionHelpers";

describe("TransformerFunctionNumberFormat", () => {
  test("inline", async () => {
    const decimals = "123456789.87654321";
    const val = BigDecimal(decimals);
    // must supply type
    await assertTransformation(val, "$$numberformat:$", decimals);
    await assertTransformation(val, "$$numberformat():$", decimals);
    //    literal
    await assertTransformation(val, "$$numberformat(DECIMAL):$", "123456789.88");
    await assertTransformation(val, "$$numberformat(DECIMAL,en-US,'#,##0.00'):$", "123,456,789.88");
    await assertTransformation(val, "$$numberformat(DECIMAL,en-US,'#,##0.00','.',','):$", "123.456.789,88");

    //    literal
    await assertTransformation(val, "$$numberformat(CURRENCY):$", "$123,456,789.88");
    await assertTransformation(val, "$$numberformat(CURRENCY,en-GB,GBP):$", "£123,456,789.88");
    await assertTransformation(val, "$$numberformat(PERCENT):$", "12,345,678,988%");
    await assertTransformation(val, "$$numberformat(INTEGER):$", "123,456,790");
    await assertTransformation(val, "$$numberformat(COMPACT):$", "123M");
    await assertTransformation(val, "$$numberformat(COMPACT,en-US,LONG):$", "123 million");
    await assertTransformation(val, "$$numberformat(COMPACT,he-IL,LONG):$", "\u200F123 מיליון");

    // BASE
    await assertTransformation(val, "$$numberformat(BASE,16):$", "75bcd15");
    await assertTransformation(val, "$$numberformat(BASE,2):$", "111010110111100110100010101");
  });
});
