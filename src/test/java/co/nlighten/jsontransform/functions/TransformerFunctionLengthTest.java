package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

public class TransformerFunctionLengthTest extends BaseTest {
    @Test
    void autoDetect() {
        var str = "Hello World";
        assertTransformation(str, "$$length:hello world", BigDecimal.valueOf(11));
        assertTransformation(str, "$$length():$", BigDecimal.valueOf(11));
        var arr = new String[]{"Hello", "World"};
        assertTransformation(arr, "$$length:$", BigDecimal.valueOf(2));
        var obj = Map.of("a", "Hello", "b", "World", "c", "foo", "d", "bar");
        assertTransformation(obj, "$$length:$", BigDecimal.valueOf(4));
    }

    @Test
    void stringOnly() {
        var str = "Hello World";
        assertTransformation(str, "$$length(STRING):hello world", BigDecimal.valueOf(11));
        assertTransformation(str, "$$length(STRING):$", BigDecimal.valueOf(11));
        var arr = new String[]{"Hello", "World"};
        assertTransformation(arr, "$$length(STRING):$", null);
        var obj = Map.of("a", "Hello", "b", "World", "c", "foo", "d", "bar");
        assertTransformation(obj, "$$length(STRING):$", null);
    }

    @Test
    void arrayOnly() {
        var str = "Hello World";
        assertTransformation(str, "$$length(ARRAY):hello world", null);
        assertTransformation(str, "$$length(ARRAY):$", null);
        var arr = new String[]{"Hello", "World"};
        assertTransformation(arr, "$$length(ARRAY):$", BigDecimal.valueOf(2));
        var obj = Map.of("a", "Hello", "b", "World", "c", "foo", "d", "bar");
        assertTransformation(obj, "$$length(ARRAY):$", null);
    }

    @Test
    void objectOnly() {
        var str = "Hello World";
        assertTransformation(str, "$$length(OBJECT):hello world", null);
        assertTransformation(str, "$$length(OBJECT):$", null);
        var arr = new String[]{"Hello", "World"};
        assertTransformation(arr, "$$length(OBJECT):$", null);
        var obj = Map.of("a", "Hello", "b", "World", "c", "foo", "d", "bar");
        assertTransformation(obj, "$$length(OBJECT):$", BigDecimal.valueOf(4));
    }

    @Test
    void zeroDefault() {
        assertTransformation(null, "$$length:$", null);
        assertTransformation(null, "$$length(AUTO,true):$", BigDecimal.ZERO);
        assertTransformation(42, "$$length(AUTO,true):$", BigDecimal.ZERO);
        assertTransformation(true, "$$length(AUTO,true):$", BigDecimal.ZERO);
    }
}
