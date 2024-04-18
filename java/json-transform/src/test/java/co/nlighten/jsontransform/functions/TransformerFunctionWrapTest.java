package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionWrapTest extends BaseTest {
    @Test
    void testInlineFunctionWrap() {
        var strVal = "text";
        assertTransformation(strVal, "$$wrap:$", strVal);
        assertTransformation(strVal, "$$wrap():$", strVal);
        assertTransformation(strVal, "$$wrap({):$", String.format("{%s", strVal));
        assertTransformation(strVal, "$$wrap(():$", String.format("(%s", strVal));
        assertTransformation(strVal, "$$wrap(,}):$", String.format("%s}", strVal));
        assertTransformation(strVal, "$$wrap(,)):$", String.format("%s)", strVal));
        assertTransformation(strVal, "$$wrap({,}):$", String.format("{%s}", strVal));
        assertTransformation(strVal, "$$wrap((,)):$", String.format("(%s)", strVal));
        assertTransformation(strVal, "$$wrap(',',):$", String.format(",%s", strVal));
        assertFailTransformation(strVal, "$$wrap(',','):$", String.format(",%s", strVal));
        assertTransformation(strVal, "$$wrap(',','):$", String.format(",%s'", strVal));
        assertTransformation(strVal, "$$wrap(',\\','):$", String.format(",',%s", strVal));
    }
}
