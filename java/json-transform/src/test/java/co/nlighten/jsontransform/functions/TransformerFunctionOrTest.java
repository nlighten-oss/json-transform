package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionOrTest extends BaseTest {
    @Test
    void assertOr() {
        var twoOrThree = fromJson("""
                                                   {
                                                   "$$or": [
                                                    { "$$is": "$[0]", "eq": 2 },
                                                    { "$$is": "$[1]", "eq": 3 }
                                                   ]
                                                   }""");
        assertTransformation(fromJson("[2,3]"), twoOrThree, true);
        assertTransformation(fromJson("[1,3]"), twoOrThree, true);
        assertTransformation(fromJson("[2,4]"), twoOrThree, true);
        assertTransformation(fromJson("[1,4]"), twoOrThree, false);
        var firstOrSecond = fromJson("""
            { "$$or": [ "$[0]", "$[1]" ] }""");
        assertTransformation(fromJson("[null,1]"), firstOrSecond, true);
        assertTransformation(fromJson("[null,null]"), firstOrSecond, false);
    }

    @Test
    void inline() {
        assertTransformation(fromJson("[null,0]"), "$$or:$", false);
        assertTransformation(fromJson("[1,0]"), "$$or:$", true);
    }
}
