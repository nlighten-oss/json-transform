package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionNotTest extends BaseTest {
    @Test
    void testInlineFunctionNot() {
        assertTransformation(true, "$$not:$", false);
        assertTransformation(false, "$$not:$", true);
    }
}
