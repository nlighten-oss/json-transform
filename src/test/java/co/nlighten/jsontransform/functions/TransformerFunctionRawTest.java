package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionRawTest extends BaseTest {
    @Test
    void object() {
        var string = "\"$$boolean:$\"";
        assertTransformation("true", fromJson("{\"$$raw\":" + string + "}"), fromJson(string));
        var object = "{\"$$first\":[\"$\",1]}";
        assertTransformation("true", fromJson("{\"$$raw\":" + object + "}"), fromJson(object));
        var array = "[\"$\"," + string + ","+ object + "]";
        assertTransformation("true", fromJson("{\"$$raw\":" + array + "}"), fromJson(array));
    }

    @Test
    void inline() {
        assertTransformation("true", "$$raw:$$boolean:$", "$$boolean:$");
        assertTransformation(null, "$$raw:$.x", "$.x");
    }
}
