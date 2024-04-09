package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TransformerFunctionReverseTest extends BaseTest {
    @Test
    void testObjectFunctionReverse() {
        assertTransformation(List.of("c", "a", "b"), fromJson("""
{
  "$$reverse": "$"
}
"""), List.of("b", "a", "c"));
        var unsortedObjects = fromJson("""
[
 { "name": "Dan" },
 { "name": "Alice" },
 { "name": "Carl" },
 { "name": "Bob" }
]""");
        assertTransformation(unsortedObjects, fromJson("""
{
  "$$reverse": "$"
}
"""), fromJson("""
[
 { "name": "Bob" },
 { "name": "Carl" },
 { "name": "Alice" },
 { "name": "Dan" }
]"""));
    }

    @Test
    void lazy() {
        assertTransformation(fromJson("[1,2,3]"), fromJson("""
{
  "$$reverse": ["$[2]","$[1]","$[0]"]
}
"""), fromJson("[1,2,3]"));
    }

    @Test
    void inline() {
        assertTransformation(List.of("c", "a", "b"), "$$reverse:$", List.of("b", "a", "c"));
    }
}
