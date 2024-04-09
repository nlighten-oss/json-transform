package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionSliceTest extends BaseTest {
    @Test
    void inline() {
        var arr = fromJson("[0, 1, 2, 3, 4, 5, 6, 7]");
        assertTransformation(arr, "$$slice():$", arr);
        assertTransformation(arr, "$$slice(1):$", fromJson("[1, 2, 3, 4, 5, 6, 7]"));
        assertTransformation(arr, "$$slice(2,6):$", fromJson("[2, 3, 4, 5]"));
        assertTransformation(arr, "$$slice(3,-1):$", fromJson("[3, 4, 5, 6]"));
        assertTransformation(arr, "$$slice(-2):$", fromJson("[6, 7]"));
        assertTransformation(arr, "$$slice(-3,-1):$", fromJson("[5, 6]"));
        assertTransformation(arr, "$$slice(-2,-1):$", fromJson("[6]"));
    }

    @Test
    void object() {
        var arr = fromJson("[0, 1, 2, 3, 4, 5, 6, 7]");
        assertTransformation(arr, fromJson("{ \"$$slice\": \"$\", \"begin\": 1 }"), fromJson("[1, 2, 3, 4, 5, 6, 7]"));
        assertTransformation(arr, fromJson("{ \"$$slice\": \"$\", \"begin\": 2, \"end\": 6 }"), fromJson("[2, 3, 4, 5]"));
        assertTransformation(arr, fromJson("{ \"$$slice\": \"$\", \"begin\": 3, \"end\": -1 }"), fromJson("[3, 4, 5, 6]"));
        assertTransformation(arr, fromJson("{ \"$$slice\": \"$\", \"begin\": -2 }"), fromJson("[6, 7]"));
        assertTransformation(arr, fromJson("{ \"$$slice\": \"$\", \"begin\": -3, \"end\": -1 }"), fromJson("[5, 6]"));
        assertTransformation(arr, fromJson("{ \"$$slice\": \"$\", \"begin\": -2, \"end\": -1 }"), fromJson("[6]"));
    }

}
