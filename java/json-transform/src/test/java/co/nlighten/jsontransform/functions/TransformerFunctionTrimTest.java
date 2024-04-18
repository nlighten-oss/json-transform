package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionTrimTest extends BaseTest {
    @Test
    void testInlineFunctionTrim() {
        var input = "  hello  ";
        assertTransformation(input, "$$trim:$", input.strip());
        assertTransformation(input, "$$trim(start):$", input.stripLeading());
        assertTransformation(input, "$$trim(END):$", input.stripTrailing());
        var inputInd = """
   <root>
     <hello />
   </root>
""";
        assertTransformation(inputInd, "$$trim(INDENT):$", inputInd.stripIndent());
    }
}
