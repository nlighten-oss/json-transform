package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionTransformTest extends BaseTest {
    @Test
    void objectForm() {
        var source = fromJson("""
{
    "value":5,
    "item":{
        "foo":"aaa",
        "id":"bbb"
    },
    "items":[
        {"foo":"bar","id":"aaa"},
        {"foo":"bar2","id":"bbb"}
    ]
}
""");

        var valueLookup = fromJson("""
{
    "$$transform": "$.item",
    "to": {
        "id":"##current.id",
        "map_foo":"##current.foo",
        "value":"$.value"
    }
}
""");
        assertTransformation(source,valueLookup,fromJson("""
{"id":"bbb","map_foo":"aaa","value":5}"""));
    }

    @Test
    void inline() {
        assertTransformation("ab","$$transform(##current[0]):$$split:$","a");
    }
}
