package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionSubstringTest extends BaseTest {
    @Test
    void testInlineFunctionSubstring() {
        var strVal = "hello-world";
        assertTransformation(strVal, "$$substring:$", strVal);
        assertTransformation(strVal, "$$substring():$", strVal);
        assertTransformation(strVal, "$$substring(5):$", strVal.substring(5));
        assertTransformation(strVal, "$$substring(5,8):$", strVal.substring(5, 8));
        assertTransformation(strVal, "$$substring(-5):$", "world");
        assertTransformation(strVal, "$$substring(5,-5):$", "-");
        assertTransformation(strVal, "$$substring(-5,-1):$", "worl");
        var numVal = 12345678;
        assertTransformation(numVal, "$$substring(0,5):$", String.valueOf(numVal).substring(0, 5));
    }
}
