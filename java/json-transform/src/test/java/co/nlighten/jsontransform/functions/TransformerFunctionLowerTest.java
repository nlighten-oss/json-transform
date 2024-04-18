package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

/**
 * @see TransformerFunctionLower
 */
public class TransformerFunctionLowerTest extends BaseTest {
    @Test
    void lowercase() {
        var strVal = "TEXT";
        assertTransformation(strVal, "$$lower:$", strVal.toLowerCase());
        assertTransformation(strVal, "$$lower():$", strVal.toLowerCase());
    }
}
