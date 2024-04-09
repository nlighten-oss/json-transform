package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionMapTest extends BaseTest {
    @Test
    void testObjectFunctionMap(){
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
        assertTransformation(source,fromJson("""
             {
                "$$map":[
                    "$.items",
                    {
                        "id":"##current.id",
                        "map_foo":"##current.foo",
                        "idx":"##index",
                        "value":"$.value"
                    }
                ]
             }
             """),fromJson("""
               [
               {"id":"aaa","map_foo":"bar","idx":0,"value":5},
               {"id":"bbb","map_foo":"bar2","idx":1,"value":5}
               ]
               """));

        assertTransformation(source,fromJson("""
             {
                "$$map":"$.items",
                "to": {
                    "id":"##current.id",
                    "map_foo":"##current.foo",
                    "idx":"##index",
                    "value":"$.value"
                }
             }
             """),fromJson("""
           [
               {"id":"aaa","map_foo":"bar","idx":0,"value":5},
               {"id":"bbb","map_foo":"bar2","idx":1,"value":5}
           ]
           """));

        var valueLookup = fromJson("""
             {
                "$$map":[
                    "$.item",
                    {
                        "id":"##current.id",
                        "map_foo":"##current.foo",
                        "idx":"##index",
                        "value":"$.value"
                    }
                ]
             }
             """);
        assertTransformation(source,valueLookup,null);
    }

    @Test
    void objectNonTransformed() {
        assertTransformation(fromJson("""
            {
                "a": [1,2],
                "b": [2,4]
            }
"""),fromJson("""
             {
                "$$map":[
                    ["$.a", "$.b"],
                    "##current[1]"
                ]
             }
             """),fromJson("[2,4]"));
    }

    @Test
    void inline() {
        assertTransformation(fromJson("""
            [{"a":10},{"a":11},{"a":12}]"""),
            "$$map(##current.a):$",
            fromJson("[10,11,12]"));
    }
}
