package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.StreamSupport;

public class TransformerFunctionFlatTest extends BaseTest {
    @Test
    void testObjectFunctionFlat() {
        var arr = new String[] { "a", "b", "c"};
        var arr2 = new String[] { "d", "e", "f"};
        var arr3 = new String[][] { arr, arr2};
        var flatCombined = Arrays.stream(arr3).flatMap(x -> StreamSupport.stream(Arrays.stream(x).spliterator(), false)).toList();
        var flatArr = Arrays.stream(arr).toList();
        assertTransformation(arr3, fromJson("""
{
  "$$flat": [ "$[0]", "$[1]" ]
}
"""), flatCombined);
        assertTransformation(arr3, fromJson("""
{
  "$$flat": [ "$[0]", "$.pointingToNowhere" ]
}
"""), flatArr);
        assertTransformation(arr3, fromJson("""
{
  "$$flat": [ ["a","b","c"], ["d","e","f"] ]
}
"""), flatCombined);
        assertTransformation(arr, fromJson("""
{
  "$$flat": [ ["a","b","c"], [] ]
}
"""), flatArr);
        assertTransformation(arr, fromJson("""
{
  "$$flat": [ ["a","b","c"], null ]
}
"""), flatArr);
    }
}
