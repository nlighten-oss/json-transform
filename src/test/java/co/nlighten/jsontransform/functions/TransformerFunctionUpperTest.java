package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionUpperTest extends BaseTest {
    @Test
    void uppercase() {
        var strVal = "text";
        assertTransformation(strVal, "$$upper:$", strVal.toUpperCase());
        assertTransformation(strVal, "$$upper():$", strVal.toUpperCase());
    }
}
