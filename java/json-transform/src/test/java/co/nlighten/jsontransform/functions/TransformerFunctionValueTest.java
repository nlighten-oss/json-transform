package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionValueTest extends BaseTest {


    record holder(Object value) {}
    @Test
    void object() {
        var arr = new holder("bbb");
        assertTransformation(arr, fromJson("""
{
 "a":"$$value:$.value",
 "b":"$$value:$.b",
 "c":{"$$value":[]},
 "d":{"$$value":{}}
}
"""), fromJson("""
                {"a":"bbb"}
                """));
    }


    @Test
    void objectStreams() {
        assertTransformation(fromJson("[1,2,3]"), fromJson("""
    {
        "a":"$$value:$"
    }"""), fromJson("""
    {"a":[1,2,3]}
                """));
        assertTransformation(fromJson("[]"), fromJson("""
    {
        "a":"$$value:$"
    }"""), fromJson("{}"));
        assertTransformation(fromJson("[1,2,3]"), fromJson("""
    {
        "a": {
          "$$value":{ "$$map": "$", "to": "##current" }
        }
    }"""), fromJson("""
    {"a":[1,2,3]}
                """));
        assertTransformation(fromJson("[]"), fromJson("""
    {
        "a": {
          "$$value":{ "$$map": "$", "to": "##current" }
        }
    }"""), fromJson("{}"));
    }
}
