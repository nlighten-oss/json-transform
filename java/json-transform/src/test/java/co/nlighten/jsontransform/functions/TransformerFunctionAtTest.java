package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionAtTest extends BaseTest {
    @Test
    void inline() {
        var arr = fromJson("[4, 2, 13]");
        assertTransformation(arr, "$$at(0):$", fromJson("4"));
        assertTransformation(arr, "$$at(1):$", fromJson("2"));
        assertTransformation(arr, "$$at(-1):$", fromJson("13"));
        assertTransformation(arr, "$$at(3):$", null);
        assertTransformation(arr, "$$at:$", null);
    }

    @Test
    void object() {
        var arr = fromJson("[4, 2, 13]");

        assertTransformation(arr, fromJson("{ \"$$at\": \"$\", \"index\": 0 }"), fromJson("4"));
        assertTransformation(arr, fromJson("{ \"$$at\": \"$\", \"index\": 1 }"), fromJson("2"));
        assertTransformation(arr, fromJson("{ \"$$at\": \"$\", \"index\": -1 }"), fromJson("13"));
        assertTransformation(arr, fromJson("{ \"$$at\": \"$\", \"index\": 3 }"), null);
        assertTransformation(arr, fromJson("""
{
  "$$at": "$"
}
"""), null);
    }

}
