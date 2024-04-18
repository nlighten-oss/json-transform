package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;
public class TransformerFunctionFormParseTest extends BaseTest {
    @Test
    void inline() {
        assertTransformation("a=1&b=B&c", "$$formparse:$", fromJson("""
            {
              "a": "1",
              "a$$": ["1"],
              "b": "B",
              "b$$": ["B"],
              "c": "true",
              "c$$": ["true"]
            }"""));

        // arrays
        assertTransformation("a=1&a=2", "$$formparse:$", fromJson("""
            {
              "a": "1",
              "a$$": ["1","2"]
            }"""));
    }

    @Test
    void object() {
        assertTransformation("a=1&b=B&c", fromJson("""
        { "$$formparse": "$" }"""), fromJson("""
            {
              "a": "1",
              "a$$": ["1"],
              "b": "B",
              "b$$": ["B"],
              "c": "true",
              "c$$": ["true"]
            }"""));

        // arrays
        assertTransformation("a=1&a=2", fromJson("""
        { "$$formparse": "$" }"""), fromJson("""
            {
              "a": "1",
              "a$$": ["1","2"]
            }"""));
    }
}
