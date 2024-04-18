package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @see TransformerFunctionMatchAll
 */
public class TransformerFunctionMatchAllTest extends BaseTest {
    @Test
    void testInlineFunctionMatch() {
        var input = "hello my helloKitty";
        assertTransformation(input, "$$matchall([el]):$", Arrays.asList("e", "l", "l", "e", "l", "l"));
        assertTransformation(input, "$$matchall([le]+):$", Arrays.asList("ell", "ell"));
        assertTransformation(input, "$$matchall(hell):$", Arrays.asList("hell","hell"));
        assertTransformation(input, "$$matchall(^hello):$", Arrays.asList("hello"));
        assertTransformation(input, "$$matchall(hello$):$", null);
    }
    @Test
    void inlineGroup() {
        assertTransformation("world to waterWorld", "$$matchall('w(\\\\w+)d',1):$", Arrays.asList("orl", "aterWorl"));
    }
}
