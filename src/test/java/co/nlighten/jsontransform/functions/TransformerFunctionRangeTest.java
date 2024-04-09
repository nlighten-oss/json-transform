package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TransformerFunctionRangeTest extends BaseTest {
    @Test
    void testInlineFunctionRange() {
        assertTransformation(null, "$$range(1,5)", fromJson("[1,2,3,4,5]"));
        assertTransformation(null, "$$range(1,5):", fromJson("[1,2,3,4,5]"));
        assertTransformation(null, "$$range(1,5,1):", fromJson("[1,2,3,4,5]"));
        assertTransformation(new BigDecimal(2.7), "$$range(1.5,$,0.5):", fromJson("[1.5,2.0,2.5]"));
        assertTransformation(null, "$$range(1,5,2)", fromJson("[1,3,5]"));
        assertTransformation(null, "$$range(10,45, 10)", fromJson("[10,20,30,40]"));
        assertTransformation(fromJson("""
{ "start":10, "end": 50, "step": 10 }"""), "$$range($.start,$.end,$.step)", fromJson("[10,20,30,40,50]"));
        // bad inputs
        assertTransformation(null, "$$range", fromJson("[]"));
        assertTransformation(null, "$$range():", fromJson("[]"));
        assertTransformation(null, "$$range(1):", fromJson("[]"));
    }
}
