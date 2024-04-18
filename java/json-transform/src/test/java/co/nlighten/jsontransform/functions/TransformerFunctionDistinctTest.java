package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionDistinctTest extends BaseTest {
    @Test
    void primitives() {
        var arr = fromJson("""
["a", "b", "c"]""");
        var arr2 = fromJson("""
["a", "b", "b", "c", "b"]""");
        assertTransformation(arr2, fromJson("""
{
  "$$distinct": "$"
}
"""), arr);

        var mix = fromJson("""
["a", 1, false, "b", "c"]""");
        var mix2 = fromJson("""
["a", 1, false, "b", "c", "b", 1, false, false]""");
        assertTransformation(mix2, fromJson("""
{
  "$$distinct": "$"
}
"""), mix);

        var withNulls = fromJson("""
["a", null]""");
        var withNulls2 = fromJson("""
["a", "a", null, null, "a", null]""");
        assertTransformation(withNulls2, fromJson("""
{
  "$$distinct": "$"
}
"""), withNulls);

        var objects = fromJson("""
[{"a": 1}]""");
        var objects2 = fromJson("""
[{"a": 1}, {"a": 1}, {"a": 1}]""");
        assertTransformation(objects2, fromJson("""
{
  "$$distinct": "$"
}
"""), objects);

        var arrays = fromJson("""
[["a",1],["a",2]]""");
        var arrays2 = fromJson("""
[["a",1],["a",2],["a",1],["a",1]]""");
        assertTransformation(arrays2, fromJson("""
{
  "$$distinct": "$"
}
"""), arrays);
    }

    @Test
    void withTransformation() {
        var objects = fromJson("""
[{"a": 1}, {"a": 1, "b": 1}, {"a": 1, "b": 2}, {"a": 2, "b": 1}]""");

        assertTransformation(objects, fromJson("""
{
  "$$distinct": "$",
  "by": "##current.a"
}
"""), fromJson("""
[{"a": 1}, {"a": 2, "b": 1}]"""));

        assertTransformation(objects, fromJson("""
{
  "$$distinct": "$",
  "by": "##current.b"
}
"""), fromJson("""
[{"a": 1}, {"a": 1, "b": 1}, {"a": 1, "b": 2}]"""));

        // without by and same arguments should stay the same
        assertTransformation(objects, fromJson("""
{
  "$$distinct": "$"
}
"""), objects);
    }

    @Test
    void inline() {
        var objects = fromJson("""
[{"a": 1}, {"a": 1, "b": 1}, {"a": 1, "b": 2}, {"a": 2, "b": 1}]""");
        assertTransformation(objects,
                             "$$distinct(##current.a):$"
                , fromJson("""
[{"a": 1}, {"a": 2, "b": 1}]"""));
    }
}
