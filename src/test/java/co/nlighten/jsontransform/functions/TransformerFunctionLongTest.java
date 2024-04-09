package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TransformerFunctionLongTest extends BaseTest {
    @Test
    void convert() {
        var decimals = "123456789.87654321";
        var longVal = BigDecimal.valueOf(new BigDecimal(decimals).longValue());
        assertTransformation(decimals, "$$long:$", longVal);
        assertTransformation(decimals, "$$long():$", longVal);
        assertTransformation(123456789.87654321, "$$long:$", longVal);
        assertTransformation(null, "$$long:$", null);
    }

    @Test
    void maxLong() {
        var max = Long.MAX_VALUE;
        assertTransformation(max, "$$long:$", fromJson(Long.toString(max)));
    }
}
