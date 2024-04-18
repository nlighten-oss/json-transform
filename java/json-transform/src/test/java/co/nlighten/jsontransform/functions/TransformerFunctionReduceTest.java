package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionReduceTest extends BaseTest {
    @Test
    void testObjectFunctionReduce(){
        var source = fromJson("""
{
    "item":{
        "aaa": { "id": 1 },
        "bbb": { "id": 2 }
    },
    "items":[
        {"id":"aaa", "amount": 10 },
        {"id":"bbb", "amount": 20 }
    ]
}
""");
        var arrLookup = fromJson("""
                                 {
                                    "$$reduce": "$.items",
                                    "identity": 0,
                                    "to": {
                                        "$$math": [
                                          "+",
                                          "##accumulator",
                                          "##current.amount"
                                        ]
                                    }
                                 }
                                 """);
        assertTransformation(source,arrLookup,fromJson("30"));



        var valueLookup = fromJson("""
                                 {
                                    "$$reduce": "aaa",
                                    "identity": "bbb",
                                    "to": "##current"
                                 }
                                 """);
        assertTransformation(source, valueLookup, null);
    }

    @Test
    void inline() {
        var source = fromJson("""
{
    "item":{
        "aaa": { "id": 1 },
        "bbb": { "id": 2 }
    },
    "items":[
        {"id":"aaa", "amount": 10 },
        {"id":"bbb", "amount": 20 }
    ]
}
""");
        var arrLookup = "$$reduce('$$math(##accumulator,+,##current.amount)',0):$.items";
        assertTransformation(source,arrLookup,fromJson("30"));

    }
}
