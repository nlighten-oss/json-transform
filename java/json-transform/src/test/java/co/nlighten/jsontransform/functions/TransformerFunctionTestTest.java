package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionTestTest extends BaseTest {
    @Test
    void testInlineFunctionTest() {
        var input = "hello";
        assertTransformation(input, "$$test([le]):$", true);
        assertTransformation(input, "$$test(hell):$", true);
        assertTransformation(input, "$$test(hello$):$", true);
        assertTransformation(input, "$$test(hell$):$", false);
        assertTransformation(input, "$$test('^hello$'):$", true);
        assertTransformation(input, "$$test('^(hello|world)$'):$", true);
        assertTransformation("HELLO", "$$test('^hello$'):$", false);
        assertTransformation("HELLO", "$$test('(?i)^hello$'):$", true);
    }
}
