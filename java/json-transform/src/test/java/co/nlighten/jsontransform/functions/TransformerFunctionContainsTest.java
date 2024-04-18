package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionContainsTest extends BaseTest {
    @Test
    void object() {
        assertTransformation(fromJson("""
[0, [], "a"]"""), fromJson("""
{
  "$$contains": "$", "that": "a"
}
"""), true);
        // with transformation
        assertTransformation("a", fromJson("""
{
  "$$contains": ["b","$"], "that": "a"
}
"""), true);

        assertTransformation(fromJson("""
[0, [], "a"]"""), fromJson("""
{
  "$$contains": "$", "that": "b"
}
"""), false);
    }

    @Test
    void inline() {
        assertTransformation(fromJson("[0, [], \"a\"]"), "$$contains(a):$", true);
        assertTransformation(fromJson("[0, [], \"a\"]"), "$$contains(b):$", false);
    }
}
