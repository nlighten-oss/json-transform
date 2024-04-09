package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class TransformerFunctionSplitTest extends BaseTest {
    private List<String> splitToList(String input, String delimiter, int limit) {
        return Arrays.stream(input.split(delimiter, limit)).toList();
    }

    @Test
    void testInlineFunctionSplit() {
        var words = "hello world";
        assertTransformation(words, "$$split:$", splitToList(words, "", 0) );
        assertTransformation(words, "$$split():$", splitToList(words, "", 0));
        assertTransformation(words, "$$split( ):$", splitToList(words, " ", 0));
        assertTransformation(words, "$$split(' '):$", splitToList(words, " ", 0));
        assertTransformation(words, "$$split(ll):$", splitToList(words, "ll", 0));
        assertTransformation(words, "$$split(ll?):$", splitToList(words, "ll?", 0));
        assertTransformation(words, "$$split(o,2):$", splitToList(words, "o", 2));
    }
}
