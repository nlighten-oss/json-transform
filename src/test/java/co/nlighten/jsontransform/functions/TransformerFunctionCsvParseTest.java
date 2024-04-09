package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;
public class TransformerFunctionCsvParseTest extends BaseTest {
    @Test
    void inline() {
        assertTransformation("a\n\",\"", "$$csvparse:$",
                             fromJson("""
                                [{ "a": "," }]"""));
        assertTransformation("a\n\"\"\"\"", "$$csvparse:$",
                             fromJson("""
                                              [{ "a": "\\"" }]"""));
        assertTransformation("1,2\n3,4", "$$csvparse(true):$",
                             fromJson("""
                                              [["1","2"],["3","4"]]"""));
    }

    @Test
    void object() {
        assertTransformation("a\n\",\"",
                             fromJson("""
                                {
                                  "$$csvparse": "$"
                                }"""),
                             fromJson("""
                                [{ "a": "," }]"""));
        assertTransformation("a\n\"\"\"\"",
                             fromJson("""
                                {
                                  "$$csvparse": "$"
                                }"""),
                             fromJson("""
                                              [{ "a": "\\"" }]"""));

    }
}
