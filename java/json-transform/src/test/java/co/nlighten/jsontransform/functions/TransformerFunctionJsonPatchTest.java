package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionJsonPatchTest extends BaseTest {

    @Test
    void add() {
        assertTransformation(fromJson("""
    {
        "a": {
            "b": "c"
        }
    }"""), fromJson("""
    {
        "$$jsonpatch": "$",
        "ops": [
            { "op":"add", "path":"/a/d", "value":"e" }
        ]
    }"""), fromJson("""
    {
        "a": {
            "b": "c",
            "d": "e"
        }
    }"""));

    }
}
