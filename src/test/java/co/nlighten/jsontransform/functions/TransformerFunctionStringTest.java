package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TransformerFunctionStringTest extends BaseTest {
    @Test
    void testInlineFunctionString() {
        var strVal = "text";
        assertTransformation(strVal, "$$string:$", strVal);
        assertTransformation(strVal, "$$string():$", strVal);
        assertTransformation(strVal, "$$string(true):$", String.format("\"%s\"", strVal));
        var boolVal = true;
        assertTransformation(boolVal, "$$string:$", String.valueOf(boolVal));
        var numVal = 123;
        assertTransformation(numVal, "$$string:$", String.valueOf(numVal));
        var decVal = new BigDecimal("3.14159265358979323846264338327950288419716939937510");
        assertTransformation(decVal, "$$string:$", "3.1415926535897932384626433832795028841971693993751");
        assertTransformation(null, "$$string:$", null);
        assertTransformation(null, "$$string(true):$", "null");
        var jsonAsString = "{\"a\":\"b\"}";
        var jsonObj = fromJson(jsonAsString);
        assertTransformation(jsonObj, "$$string:$", jsonAsString);
    }

    @Test
    void mathIntegers() {
        int a = 100;
        assertTransformation(a, "$$string:$", "100");
        long b = 100;
        assertTransformation(b, "$$string:$", "100");
        Float c = 100f;
        assertTransformation(c, "$$string:$", "100");
        Double d = 100d;
        assertTransformation(d, "$$string:$", "100");
        BigInteger e = new BigInteger("100");
        assertTransformation(e, "$$string:$", "100");
        BigDecimal f = new BigDecimal("100");
        assertTransformation(f, "$$string:$", "100");
        assertTransformation(fromJson("100"), "$$string:$", "100");
        assertTransformation(fromJson("100.00"), "$$string:$", "100");
    }
}