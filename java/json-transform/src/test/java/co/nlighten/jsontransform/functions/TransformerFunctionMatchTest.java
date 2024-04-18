package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

/**
 * @see TransformerFunctionMatch
 */
public class TransformerFunctionMatchTest extends BaseTest {
    @Test
    void testInlineFunctionMatch() {
        var input = "hello";
        assertTransformation(input, "$$match([le]):$", "e");
        assertTransformation(input, "$$match([le]+):$", "ell");
        assertTransformation(input, "$$match(hell):$", "hell");
        assertTransformation(input, "$$match(hello$):$", "hello");
        assertTransformation(input, "$$match(hell$):$", null);
    }
    @Test
    void inlineGroup() {
        assertTransformation("world", "$$match('w(\\\\w+)d',1):$", "orl");
    }
}
