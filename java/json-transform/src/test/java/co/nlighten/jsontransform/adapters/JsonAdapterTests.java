package co.nlighten.jsontransform.adapters;

import co.nlighten.jsontransform.AsyncJsonElementStreamer;
import co.nlighten.jsontransform.MultiAdapterBaseTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;

public class JsonAdapterTests extends MultiAdapterBaseTest {
    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testAsString_BigDecimal(JsonAdapter<?,?,?> adapter) {
        var x = BigDecimal.valueOf(3);
        assertEquals(adapter, "3", adapter.getAsString(x));
        x = BigDecimal.valueOf(0.5);
        assertEquals(adapter, "0.5", adapter.getAsString(x));
        x = BigDecimal.valueOf(Double.MAX_VALUE);
        assertEquals(adapter, String.format("%.0f",x), adapter.getAsString(x));
        var str = "98765432101234567890000000000000.9876543210123456789";
        x = new BigDecimal(str);
        assertEquals(adapter, str, adapter.getAsString(x));
        str = "1.5E+50";
        x = new BigDecimal(str);
        assertEquals(adapter, x.toPlainString(), adapter.getAsString(x));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testAsString_Float(JsonAdapter<?,?,?> adapter) {
        float f = 3.0f;
        assertEquals(adapter, "3", adapter.getAsString(f));
        f = 0.5f;
        assertEquals(adapter, "0.5", adapter.getAsString(f));
        f = Float.MAX_VALUE;
        assertEquals(adapter, String.format("%.0f",f), adapter.getAsString(f));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testAsString_Double(JsonAdapter<?,?,?> adapter) {
        double d = 3.0d;
        assertEquals(adapter, "3", adapter.getAsString(d));
        d = 0.5f;
        assertEquals(adapter, "0.5", adapter.getAsString(d));
        d = Double.MAX_VALUE;
        assertEquals(adapter, String.format("%.0f",d), adapter.getAsString(d));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testMergeIntoGivenMutuallyExclusiveKeysWithDot(JsonAdapter<?,?,?> adapter) {
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
        assertEquals(adapter, expected, adapter.mergeInto(root, mergee, null));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testMergeIntoGivenNoPath(JsonAdapter<?,?,?> adapter) {
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
        assertEquals(adapter, expected, adapter.mergeInto(root, mergee, null));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testMergeIntoGivenMutuallyExclusiveKeysAndDollarPath(JsonAdapter<?,?,?> adapter) {
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
        assertEquals(adapter, expected, adapter.mergeInto(root, mergee, "$"));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testArraySetOnOutOfBoundsIndex(JsonAdapter<?,?,?> adapter) {
        var array = adapter.createArray(4);
        var el = adapter.wrap("string");
        adapter.set(array, 10, el);
        assertEquals(adapter, el, adapter.get(array, 10));
    }
}
