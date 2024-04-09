package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionConcatTest extends BaseTest {
    @Test
    void testObjectFunctionConcat() {
        var arr = new String[] { "a", "b", "c"};
        var arr2 = new String[] { "d", "e", "f"};
        var arr3 = new String[][] { arr, arr2};
        assertTransformation(arr3, fromJson("""
{
  "$$concat": [ "$[0]", "$[1]" ]
}
"""), fromJson("""
["a","b","c","d","e","f"]"""));

        // keep nulls
        assertTransformation(arr3, fromJson("""
{
  "$$concat": [ ["a",null,"c"], ["d","e",null] ]
}
"""), fromJson("""
["a",null,"c","d","e",null]"""));

        // skip nulls
        assertTransformation(arr, fromJson("""
{
  "$$concat": [ ["a","b","c"], null ]
}
"""), fromJson("""
["a","b","c"]"""));

        // append non nulls
        assertTransformation(arr, fromJson("""
{
  "$$concat": [ ["a","b","c"], "d", ["e"] ]
}
"""), fromJson("""
["a","b","c","d","e"]"""));

        // blind concat
        assertTransformation(fromJson("[ [\"a\",\"b\",\"c\"], \"d\", [\"e\"] ]"), fromJson("""
{
  "$$concat": "$"
}
"""), fromJson("""
["a","b","c","d","e"]"""));
    }
}
