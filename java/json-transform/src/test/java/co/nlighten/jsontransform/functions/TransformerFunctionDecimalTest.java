package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TransformerFunctionDecimalTest extends BaseTest {
    @Test
    void convert() {
        var decimals = "123456789.87654321";
        var val = new BigDecimal(decimals);
        assertTransformation(decimals, "$$decimal:$", val);
        assertTransformation(decimals, "$$decimal():$", val);
        assertTransformation(123456789.87654321, "$$decimal:$", val);
        assertTransformation(null, "$$decimal:$", null);
    }

    @Test
    void scaling() {
        var decimals = "123456789.87654321";
        assertTransformation(decimals, "$$decimal(2):$", new BigDecimal("123456789.88"));
        assertTransformation(decimals, "$$decimal(2,FLOOR):$", new BigDecimal("123456789.87"));
        var overmax = "1.01234567890123456789";
        assertTransformation(overmax, "$$decimal:$", new BigDecimal("1.012345678901235"));
    }

    @Test
    void object() {
        var decimals = "123456789.87654321";
        assertTransformation(decimals, fromJson("{\"$$decimal\":\"$\",\"scale\":2}"),  new BigDecimal("123456789.88"));
        assertTransformation(decimals, fromJson("{\"$$decimal\":\"$\",\"scale\":2,\"rounding\":\"FLOOR\"}"),  new BigDecimal("123456789.87"));
        var overmax = "1.01234567890123456789";
        assertTransformation(overmax, fromJson("{\"$$decimal\":\"$\"}"), new BigDecimal("1.012345678901235"));
    }
}
