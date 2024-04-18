package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;
public class TransformerFunctionFormTest extends BaseTest {
    @Test
    void inline() {
        assertTransformation(fromJson("""
            {
              "a": 1,
              "b": "B",
              "c":true
            }"""), "$$form:$", "a=1&b=B&c=true");

        // arrays
        assertTransformation(fromJson("""
            {
              "a": [1,2],
              "c":true
            }"""), "$$form:$", "a=1&a=2&c=true");
    }

    @Test
    void object() {
        assertTransformation(fromJson("""
             {
              "a": 1,
              "b": "B",
              "c":true
            }"""), fromJson("""
        { "$$form": "$" }"""), "a=1&b=B&c=true");

        // arrays
        assertTransformation(fromJson("""
            {
              "a": [1,2],
              "c":true
            }"""), fromJson("""
        { "$$form": "$" }"""), "a=1&a=2&c=true");
    }
}
