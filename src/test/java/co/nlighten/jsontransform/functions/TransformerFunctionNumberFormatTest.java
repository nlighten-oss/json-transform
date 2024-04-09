package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import co.nlighten.jsontransform.functions.common.FunctionHelpers;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class TransformerFunctionNumberFormatTest extends BaseTest {

    @Test
    void apply() {
        var decimals = "123456789.87654321";
        var val = new BigDecimal(decimals);
        // must supply type
        assertTransformation(val, "$$numberformat:$", NumberFormat.getNumberInstance().format(val));
        assertTransformation(val,"$$numberformat():$", NumberFormat.getNumberInstance().format(val));
        // DECIMAL
        assertTransformation(val,"$$numberformat(DECIMAL):$", FunctionHelpers.getDecimalFormatter(FunctionHelpers.DEFAULT_LOCALE, "#0.00", null, null).format(val));
        assertTransformation(val,"$$numberformat(DECIMAL,en-US,'#,##0.00'):$", FunctionHelpers.getDecimalFormatter(Locale.forLanguageTag("en-US"), "#,##0.00", null, null).format(val));
        assertTransformation(val,"$$numberformat(DECIMAL,en-US,'#,##0.00','.',','):$", FunctionHelpers.getDecimalFormatter(Locale.forLanguageTag("en-US"), "#,##0.00", ".", ",").format(val));
        //    literal
        assertTransformation(val,"$$numberformat(DECIMAL):$", "123456789.88");
        assertTransformation(val,"$$numberformat(DECIMAL,en-US,'#,##0.00'):$", "123,456,789.88");
        assertTransformation(val,"$$numberformat(DECIMAL,en-US,'#,##0.00','.',','):$", "123.456.789,88");

        //
        assertTransformation(val, "$$numberformat(CURRENCY):$", NumberFormat.getCurrencyInstance(FunctionHelpers.DEFAULT_LOCALE).format(val));
        assertTransformation(val, "$$numberformat(CURRENCY,en-GB):$", NumberFormat.getCurrencyInstance(Locale.UK).format(val));
        assertTransformation(val,"$$numberformat(PERCENT):$", NumberFormat.getPercentInstance(FunctionHelpers.DEFAULT_LOCALE).format(val));
        assertTransformation(val,"$$numberformat(INTEGER):$", NumberFormat.getIntegerInstance(FunctionHelpers.DEFAULT_LOCALE).format(val));
        assertTransformation(val,"$$numberformat(COMPACT):$", NumberFormat.getCompactNumberInstance(FunctionHelpers.DEFAULT_LOCALE, NumberFormat.Style.SHORT).format(val));
        assertTransformation(val,"$$numberformat(COMPACT,en-US,LONG):$", NumberFormat.getCompactNumberInstance(Locale.forLanguageTag("en-US"), NumberFormat.Style.LONG).format(val));
        //    literal
        assertTransformation(val, "$$numberformat(CURRENCY):$", "$123,456,789.88");
        assertTransformation(val, "$$numberformat(CURRENCY,en-GB):$", "£123,456,789.88");
        assertTransformation(val,"$$numberformat(PERCENT):$", "12,345,678,988%");
        assertTransformation(val,"$$numberformat(INTEGER):$", "123,456,790");
        assertTransformation(val,"$$numberformat(COMPACT):$", "123M");
        assertTransformation(val,"$$numberformat(COMPACT,en-US,LONG):$", "123 million");
        assertTransformation(val,"$$numberformat(COMPACT,he-IL,LONG):$", "\u200F123 מיליון");

        // BASE
        assertTransformation(val,"$$numberformat(BASE,16):$", "75bcd15");
        assertTransformation(val,"$$numberformat(BASE,2):$", "111010110111100110100010101");
    }
}
