package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;
public class TransformerFunctionObjectTest extends BaseTest {
    @Test
    void inline() {
        assertTransformation(fromJson("""
             [["a",1],["b",true],["c","C"]]"""),
            "$$object:$", fromJson("""
             {
              "a": 1,
              "b": true,
              "c": "C"
             }"""));
        assertTransformation(fromJson("""
             [[0, 1],[1, true],[2, "C"]]"""),
             "$$object:$", fromJson("""
             { 0:1, 1:true, 2:"C"}"""));

        // invalid input will yield an empty object
        assertTransformation(null, "$$object", fromJson("{}"));
        assertTransformation(0.5, "$$object:$", fromJson("{}"));
        assertTransformation("test", "$$object:$", fromJson("{}"));
        assertTransformation(false, "$$object:$", fromJson("{}"));
    }

    @Test
    void object() {
        assertTransformation(fromJson("""
             [["a",1],["b",true],["c","C"]]"""),
                             fromJson("""
             { "$$object": "$" }"""), fromJson("""
             {
              "a": 1,
              "b": true,
              "c": "C"
             }"""));
        assertTransformation(fromJson("""
             [[0, 1],[1, true],[2, "C"]]"""),
                             fromJson("""
             { "$$object": "$" }"""), fromJson("""
             { 0:1, 1:true, 2:"C"}"""));
        assertTransformation(null, fromJson("""
            { "$$object": "$"}"""), fromJson("{}"));
        assertTransformation(0.5, fromJson("""
            { "$$object": "$"}"""), fromJson("{}"));
        assertTransformation(false, fromJson("""
            { "$$object": "$"}"""), fromJson("{}"));
        // explicit

        assertTransformation(fromJson("""
             {
              "key": 1,
              "value": true
             }"""), fromJson("""
             { "$$object": [["$.key", "$.value"]]}"""),  fromJson("""
             { 1: true }"""));

        assertTransformation(fromJson("""
             {
              "key": "a",
              "value": 0.5
             }"""), fromJson("""
             { "$$object": [["$.key", "$.value"]]}"""),  fromJson("""
             { "a": 0.5 }"""));
    }
}
