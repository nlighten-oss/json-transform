package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionPadTest extends BaseTest {
    @Test
    void testInlineFunctionPad() {
        var strVal = "text";
        assertTransformation(strVal, "$$pad(right,3,2):$", strVal);
        assertTransformation(strVal, "$$pad(end,6):" + strVal, strVal + "00");
        assertTransformation(strVal, "$$pad(end,6):$", strVal + "00");
        assertTransformation(strVal, "$$pad(right,6,0):$", strVal + "00");
        assertTransformation(strVal, "$$pad(end,6,' '):$", strVal + "  ");
        assertTransformation(strVal, "$$pad(left,6,' '):$", "  " + strVal);
        assertTransformation(strVal, "$$pad(left,6,x):$", "xx" + strVal);
        assertTransformation(strVal, "$$pad(start,6,xy):$", "xy" + strVal);
        assertTransformation(strVal, "$$pad(start,9,xy):$", "xyxyx" + strVal);

        // bad inputs
        assertTransformation(strVal, "$$pad:$", strVal);
        assertTransformation(strVal, "$$pad:$", strVal);
        assertTransformation(strVal, "$$pad():$", strVal);
        assertTransformation(strVal, "$$pad(start):$", strVal);
    }
}
