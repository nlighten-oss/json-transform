package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import co.nlighten.jsontransform.functions.common.FunctionHelpers;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Locale;

public class TransformerFunctionNumberParseTest extends BaseTest {
    @Test
    void apply() {
        var decimals = "123456789.88";
        var val = new BigDecimal(decimals);

        assertTransformation(decimals, "$$numberparse:$", val);
        assertTransformation(decimals,"$$numberparse():$", val);
        //
        assertTransformation(FunctionHelpers.getDecimalFormatter(FunctionHelpers.DEFAULT_LOCALE, "#,##0.00", null, null).format(val),"$$numberparse('#,##0.00'):$", val);
        assertTransformation(FunctionHelpers.getDecimalFormatter(Locale.forLanguageTag("en-US"), "#,##0.00", ".", ",").format(val),"$$numberparse('#,##0.00',en-US,'.',','):$", val);
        // literal
        assertTransformation("123,456,789.88","$$numberparse('#,##0.00'):$", val);
        assertTransformation("123.456.789,88","$$numberparse('#,##0.00',en-US,'.',','):$", val);

        // HEX
        var hex = "75bcd15";
        assertTransformation(hex,"$$numberparse(BASE,16):$", fromJson("123456789"));
        var binary = "00001010";
        assertTransformation(binary,"$$numberparse(BASE,2):$", fromJson("10"));
    }
}
