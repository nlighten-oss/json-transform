package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionReplaceTest extends BaseTest {
    @Test
    void testInlineFunctionReplace() {
        var input = "hello";
        assertTransformation(input, "$$replace:$", input);
        assertTransformation(input, "$$replace(l):$", "heo");
        assertTransformation(input, "$$replace(l,x):$", "hexxo");
        assertTransformation(input, "$$replace(l,x,FIRST):$", "hexlo");
        assertTransformation(input, "$$replace(l,x,FIRST,3):$", "helxo");
        assertTransformation(input, "$$replace([le],x,REGEX):$", "hxxxo");
        assertTransformation(input, "$$replace([le],x,REGEX,2):$", "hexxo");
        assertTransformation(input, "$$replace([le],x,REGEX-FIRST):$", "hxllo");
        assertTransformation(input, "$$replace([le],x,REGEX-FIRST,2):$", "hexlo");
    }
}
