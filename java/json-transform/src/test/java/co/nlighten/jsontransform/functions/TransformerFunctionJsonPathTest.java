package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionJsonPathTest extends BaseTest {

    @Test
    void yoDawg() {
        assertTransformation(fromJson("""
        {
            "path": "$.path"
        }"""), "$$jsonpath($.path):$", "$.path");
    }

    @Test
    void inArray() {
        assertTransformation(fromJson("""
        {
            "arr": [
                null,
                "boo"
            ]
        }"""), "$$jsonpath('\\\\$.arr[1]'):$", "boo");
    }

    @Test
    void multipleResults() {
        assertTransformation(fromJson("""
        [
            { "id": 1, "active": true },
            { "id": 3, "active": false },
            { "id": 4, "active": true },
            { "id": 5, "active": false }
        ]"""), "$$jsonpath('\\\\$[?(@.active == true)]'):$", fromJson("""
        [
            { "id": 1, "active": true },
            { "id": 4, "active": true }
        ]"""));
    }
}
