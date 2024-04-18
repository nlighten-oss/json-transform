package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionFlattenTest extends BaseTest {


    record holder(Object value) {}
    @Test
    void object() {
        var arr = new holder("bbb");
        assertTransformation(arr, fromJson("""
{
  "$$flatten":{"a":{"a1":123,"a2":[1,2,3,{"c":true}]},"b":"$.value"},
  "array_prefix":"\\\\$"
}
"""), fromJson("""
                {"a.a1":123,"a.a2.$0":1,"a.a2.$1":2,"a.a2.$2":3,"a.a2.$3.c":true,"b":"bbb"}
                """));



        assertTransformation(arr, fromJson("""
{
  "$$flatten":{"a":{"a1":123,"a2":[1,2,3,{"c":true}]},"b":"$.value"},
  "prefix":"xxx",
  "array_prefix":""
}
"""), fromJson("""
                {"xxx.a.a1":123,"xxx.a.a2.0":1,"xxx.a.a2.1":2,"xxx.a.a2.2":3,"xxx.a.a2.3.c":true,"xxx.b":"bbb"}
                """));



        assertTransformation(arr, fromJson("""
{
  "$$flatten":{"a":{"a1":123,"a2":[1,2,3,{"c":true}]},"b":"$.value"},
  "prefix":"xxx",
  "array_prefix":"#null"
}
"""), fromJson("""
                {"xxx.a.a1":123,"xxx.a.a2":[1,2,3,{"c":true}],"xxx.b":"bbb"}
                """));





    }

}
