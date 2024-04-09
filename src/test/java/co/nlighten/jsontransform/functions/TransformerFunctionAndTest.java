package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionAndTest extends BaseTest {
    @Test
    void assertAnd() {
        var twoAndThree = fromJson("""
                                                   {
                                                   "$$and": [
                                                    { "$$is": "$[0]", "eq": 2 },
                                                    { "$$is": "$[1]", "eq": 3 }
                                                   ]
                                                   }""");
        assertTransformation(fromJson("[2,3]"), twoAndThree, true);
        assertTransformation(fromJson("[2,4]"), twoAndThree, false);
    }

    @Test
    void inline() {
        assertTransformation(fromJson("[null,0]"), "$$and:$", false);
        assertTransformation(fromJson("[1,0]"), "$$and:$", false);
        assertTransformation(fromJson("[1,true]"), "$$and:$", true);
    }
}
