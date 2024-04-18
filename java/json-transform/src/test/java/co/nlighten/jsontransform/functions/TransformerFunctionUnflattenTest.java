package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionUnflattenTest extends BaseTest {


    record holder(Object value) {}
    @Test
    void object() {
        var arr = new holder("bbb");
        assertTransformation(arr, fromJson("""
{
  "$$unflatten":{"a.a1":123,"a.a2":[1,2,3,{"c":true}],"b":"$.value"}
}
"""), fromJson("""
                {"a":{"a1":123,"a2":[1,2,3,{"c":true}]},"b":"bbb"}
                """));


    }

}
