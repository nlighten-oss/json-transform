package co.nlighten.jsontransform.adapters;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class JsonAdapterTests extends BaseTest {
    
    @Test
    void testAsString_BigDecimal() {
        var x = BigDecimal.valueOf(3);
        assertEquals("3", adapter.getAsString(x));
        x = BigDecimal.valueOf(0.5);
        assertEquals("0.5", adapter.getAsString(x));
        x = BigDecimal.valueOf(Double.MAX_VALUE);
        assertEquals(String.format("%.0f",x), adapter.getAsString(x));
        var str = "98765432101234567890000000000000.9876543210123456789";
        x = new BigDecimal(str);
        assertEquals(str, adapter.getAsString(x));
        str = "1.5E+50";
        x = new BigDecimal(str);
        assertEquals(x.toPlainString(), adapter.getAsString(x));
    }

    @Test
    void testAsString_Float() {
        float f = 3.0f;
        assertEquals("3", adapter.getAsString(f));
        f = 0.5f;
        assertEquals("0.5", adapter.getAsString(f));
        f = Float.MAX_VALUE;
        assertEquals(String.format("%.0f",f), adapter.getAsString(f));
    }

    @Test
    void testAsString_Double() {
        double d = 3.0d;
        assertEquals("3", adapter.getAsString(d));
        d = 0.5f;
        assertEquals("0.5", adapter.getAsString(d));
        d = Double.MAX_VALUE;
        assertEquals(String.format("%.0f",d), adapter.getAsString(d));
    }

    @Test
    void testMergeIntoGivenMutuallyExclusiveKeysWithDot() {
        var root = adapter.parse("""
{
    "numbers.roman": { "I": 1, "II": 2 }
}
""");
        var mergee = adapter.parse("""
{
    "numbers.exist": true
}
""");
        var expected = adapter.parse("""
{
    "numbers.roman": { "I": 1, "II": 2 },
    "numbers.exist": true
}
""");
        assertEquals(expected, adapter.mergeInto(root, mergee, null));
    }

    @Test
    void testMergeIntoGivenNoPath() {
        var root = adapter.parse("""
{
    "a.b.c[0]": "foovalue"
}
""");
        var mergee = adapter.parse("""
{
    "a": { "z": "barvalue" }
}
""");
        var expected = adapter.parse("""
{
    "a": { "z": "barvalue" }, "a.b.c[0]": "foovalue"
}
""");
        assertEquals(expected, adapter.mergeInto(root, mergee, null));
    }

    @Test
    void testMergeIntoGivenMutuallyExclusiveKeysAndDollarPath() {
        var root = adapter.parse("""
{
    "roman": { "I": 1, "II": 2 }
}
""");
        var mergee = adapter.parse("""
{
    "arithmetics": { "exist": true },
    "symbols": ["I", "V", "X", "L", "C", "D", "M"]
}
""");
        var expected = adapter.parse("""
{
    "roman": { "I": 1, "II": 2 },
    "arithmetics": { "exist": true },
    "symbols": ["I", "V", "X", "L", "C", "D", "M"]
}
""");
        assertEquals(expected, adapter.mergeInto(root, mergee, "$"));
    }
}
