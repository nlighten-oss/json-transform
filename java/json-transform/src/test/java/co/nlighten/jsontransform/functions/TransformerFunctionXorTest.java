package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionXorTest extends BaseTest {
    @Test
    void assertOr() {
        var first_2_XOR_second_3 = fromJson("""
            {
              "$$xor": [
                { "$$is": "$[0]", "eq": 2 },
                { "$$is": "$[1]", "eq": 3 }
              ]
            }""");
        assertTransformation(fromJson("[2,3]"), first_2_XOR_second_3, false);
        assertTransformation(fromJson("[1,3]"), first_2_XOR_second_3, true);
        assertTransformation(fromJson("[2,4]"), first_2_XOR_second_3, true);
        assertTransformation(fromJson("[1,4]"), first_2_XOR_second_3, false);

        var firstXorSecond = fromJson("""
            { "$$xor": [ "$[0]", "$[1]" ] }""");
        assertTransformation(fromJson("[1,1]"), firstXorSecond, false);
        assertTransformation(fromJson("[null,null]"), firstXorSecond, false);
        assertTransformation(fromJson("[null,1]"), firstXorSecond, true);
    }

    @Test
    void inline() {
        assertTransformation(fromJson("[null,0]"), "$$xor:$", false);
        assertTransformation(fromJson("[1,1]"), "$$xor:$", false);
        assertTransformation(fromJson("[1,0]"), "$$xor:$", true);
        assertTransformation(fromJson("[1,null]"), "$$xor:$", true);
    }
}
