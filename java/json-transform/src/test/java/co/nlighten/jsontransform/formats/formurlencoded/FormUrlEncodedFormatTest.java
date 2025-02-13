package co.nlighten.jsontransform.formats.formurlencoded;

import co.nlighten.jsontransform.MultiAdapterBaseTest;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class FormUrlEncodedFormatTest extends MultiAdapterBaseTest {

    public static class FUETest {
        public String title = "Hello World";
        public int[] numbers = new int[] { 1, 2};
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testSerialize(JsonAdapter<?,?,?> adapter) {
        var xbt = new FormUrlEncodedFormat(adapter);
        var result = xbt.serialize(new FUETest());

        var expect = "title=Hello+World&numbers=1&numbers=2";
        var expectAlt = "numbers=1&numbers=2&title=Hello+World";

        Assertions.assertTrue(expect.equals(result) || expectAlt.equals(result),
                () -> "Expected: <" + expect + ">, Actual: <" + result + ">");

        var test2 = new FUETest();
        test2.title = "not url+safe?=x&b=path/path";
        var result2 = xbt.serialize(test2);

        var expect2 = "title=not+url%2Bsafe%3F%3Dx%26b%3Dpath%2Fpath&numbers=1&numbers=2";
        var expect2Alt = "numbers=1&numbers=2&title=not+url%2Bsafe%3F%3Dx%26b%3Dpath%2Fpath";

        Assertions.assertTrue(expect2.equals(result2) || expect2Alt.equals(result2),
                () -> "Expected: <" + expect2 + ">, Actual: <" + result2 + ">");

    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testDeserialize(JsonAdapter<?,?,?> adapter) {
        var xbt = new FormUrlEncodedFormat(adapter);
        var result = xbt.deserialize("a=1&b=hello&c");
        assertEquals(adapter, adapter.parse("""
                                                             {
                                                               "a": "1",
                                                               "a$$": ["1"],
                                                               "b": "hello",
                                                               "b$$": ["hello"],
                                                               "c": "true",
                                                               "c$$": ["true"]
                                                             }"""), result);

        var result2 = xbt.deserialize("a=one&b=a&b=b&b=c");
        assertEquals(adapter, adapter.parse("""
                                                             {
                                                               "a": "one",
                                                               "a$$": ["one"],
                                                               "b": "a",
                                                               "b$$": ["a","b","c"]
                                                             }"""), result2);

        var result3 = xbt.deserialize("c&c&d=Hello+World&title=not+url%2Bsafe%3F%3Dx%26b%3Dpath%2Fpath");
        assertEquals(adapter, adapter.parse("""
                                                             {
                                                               "c":"true",
                                                               "c$$": ["true","true"],
                                                               "d": "Hello World",
                                                               "d$$": ["Hello World"],
                                                               "title": "not url+safe?=x&b=path/path",
                                                               "title$$": ["not url+safe?=x&b=path/path"]
                                                             }"""), result3);
    }

}
