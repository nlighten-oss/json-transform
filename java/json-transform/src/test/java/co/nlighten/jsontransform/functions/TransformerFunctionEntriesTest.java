package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;
public class TransformerFunctionEntriesTest extends BaseTest {
    @Test
    void inline() {
        assertTransformation(fromJson("""
             {
              "a": 1,
              "b": true,
              "c": "C"
             }"""), "$$entries:$", fromJson("""
             [["a",1],["b",true],["c","C"]]"""));
        assertTransformation(fromJson("""
             [1, true, "C"]"""), "$$entries:$", fromJson("""
             [[0, 1],[1, true],[2, "C"]]"""));
    }

    @Test
    void object() {
        assertTransformation(fromJson("""
             {
              "a": 1,
              "b": true,
              "c": "C"
             }"""), fromJson("""
             { "$$entries": "$" }"""),  fromJson("""
             [["a",1],["b",true],["c","C"]]"""));

        assertTransformation(fromJson("""
             [1, true, "C"]"""), fromJson("""
             { "$$entries": "$" }"""),  fromJson("""
             [[0, 1],[1, true],[2, "C"]]"""));

        // explicit

        assertTransformation(fromJson("""
             {
              "a": 1,
              "b": true,
              "c": "C"
             }"""), fromJson("""
             { "$$entries": { "*": "$", "d": 0.5 } }"""),  fromJson("""
             [["a",1],["b",true],["c","C"],["d",0.5]]"""));

    }
}
