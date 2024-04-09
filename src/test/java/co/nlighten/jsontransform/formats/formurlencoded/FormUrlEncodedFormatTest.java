package co.nlighten.jsontransform.formats.formurlencoded;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FormUrlEncodedFormatTest extends BaseTest {

    public static class FUETest {
        public String title = "Hello World";
        public int[] numbers = new int[] { 1, 2};
    }

    @Test
    void testSerialize() {
        var xbt = new FormUrlEncodedFormat<>(adapter);
        var result = xbt.serialize(new FUETest());

        var expect = """
title=Hello+World&numbers=1&numbers=2""";

        Assertions.assertEquals(expect, result);

        var test2 = new FUETest();
        test2.title = "not url+safe?=x&b=path/path";
        var result2 = xbt.serialize(test2);

        var expect2 = "title=not+url%2Bsafe%3F%3Dx%26b%3Dpath%2Fpath&numbers=1&numbers=2";

        Assertions.assertEquals(expect2, result2);

    }

    @Test
    void testDeserialize() {
        var xbt = new FormUrlEncodedFormat<>(adapter);
        var result = adapter.unwrap(xbt.deserialize("a=1&b=hello&c"), false);
        Assertions.assertEquals(fromJson("""
                                                             {
                                                               "a": "1",
                                                               "a$$": ["1"],
                                                               "b": "hello",
                                                               "b$$": ["hello"],
                                                               "c": "true",
                                                               "c$$": ["true"]
                                                             }"""), result);

        var result2 = adapter.unwrap(xbt.deserialize("a=one&b=a&b=b&b=c"), false);
        Assertions.assertEquals(fromJson("""
                                                             {
                                                               "a": "one",
                                                               "a$$": ["one"],
                                                               "b": "a",
                                                               "b$$": ["a","b","c"]
                                                             }"""), result2);

        var result3 = adapter.unwrap(xbt.deserialize("c&c&d=Hello+World&title=not+url%2Bsafe%3F%3Dx%26b%3Dpath%2Fpath"), false);
        Assertions.assertEquals(fromJson("""
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
