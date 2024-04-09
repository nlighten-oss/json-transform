package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

public class TransformerFunctionBooleanTest extends BaseTest {
    @Test
    void truthy() {
        assertTransformation(true, "$$boolean:$", true);
        // string
        assertTransformation("0", "$$boolean(js):$", true);
        assertTransformation("false", "$$boolean(js):$", true);
        assertTransformation("true", "$$boolean:$", true);
        assertTransformation("True", "$$boolean:$", true);
        assertTransformation("true", "$$boolean(JS):$", true);
        // number
        assertTransformation(1, "$$boolean:$", true);
        assertTransformation(-1, "$$boolean:$", true);
        assertTransformation(new BigDecimal("1"), "$$boolean:$", true);
        // object
        assertTransformation(fromJson("{\"\":0}"), "$$boolean:$", true);
        // arrays
        assertTransformation(new int[]{0}, "$$boolean:$", true);
        assertTransformation(List.of(0), "$$boolean:$", true);
    }
    @Test
    void falsy() {
        assertTransformation(false, "$$boolean:$", false);
        // string
        assertTransformation("", "$$boolean:$", false);
        assertTransformation("", "$$boolean(js):$", false);
        assertTransformation("0", "$$boolean:$", false);
        assertTransformation("false", "$$boolean:$", false);
        assertTransformation("False", "$$boolean:$", false);
        // number
        assertTransformation(0, "$$boolean:$", false);
        assertTransformation(BigDecimal.ZERO, "$$boolean:$", false);
        // object
        assertTransformation(null, "$$boolean:$", false);
        assertTransformation(fromJson("{}"), "$$boolean:$", false);
        // arrays
        assertTransformation(new int[] {}, "$$boolean:$", false);
        assertTransformation(List.of(), "$$boolean:$", false);
    }

    @Test
    void object() {
        assertTransformation("true", fromJson("{\"$$boolean\":\"$\",\"style\":\"JS\"}"), true);
        assertTransformation("false", fromJson("{\"$$boolean\":\"$\",\"style\":\"js\"}"), true);
        assertTransformation("false", fromJson("{\"$$boolean\":\"$\"}"), false);
    }
}
