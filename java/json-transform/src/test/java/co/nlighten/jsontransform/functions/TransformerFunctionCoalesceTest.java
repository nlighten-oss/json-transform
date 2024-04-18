package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionCoalesceTest extends BaseTest {
    @Test
    void testObjectFunctionCoalesce() {
        var arr = new String[] { null, null, "c"};
        assertTransformation(arr, fromJson("""
{
  "$$coalesce": "$"
}
"""), arr[2]);
        assertTransformation(arr, fromJson("""
{
  "$$coalesce": ["$[0]", "b", "c"]
}
"""), "b");
        // alias
        assertTransformation(arr, fromJson("""
{
  "$$first": "$"
}
"""), arr[2]);
    }
}
