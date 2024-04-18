package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionFilterTest extends BaseTest {
    @Test
    void truthyValuesOnly() {
        assertTransformation(fromJson("""
["a", true, "false", 0, 1, [], [0]]"""), fromJson("""
{
  "$$filter": "$", "by": "##current"
}
"""), fromJson("""
["a", true, "false", 1, [0]]"""));

        // explicit boolean (non js style)
        assertTransformation(fromJson("""
["a", true, "false", 0, 1, [], [0]]"""), fromJson("""
{
  "$$filter": "$", "by": "$$boolean:##current"
}
"""), fromJson("""
[true, 1, [0]]"""));
    }
    @Test
    void namesThatStartsWithA() {
        assertTransformation(fromJson("""
[{"name":"alice"}, {"name":"ann"}, {"name":"bob"}]"""), fromJson("""
{
  "$$filter": "$", "by": "$$test(^a):##current.name"
}
"""), fromJson("""
[{"name":"alice"}, {"name":"ann"}]"""));
    }

    @Test
    void inline() {
        assertTransformation(fromJson("[\"a\", true, \"false\", 0, 1, [], [0]]"),
            "$$filter(##current):$",
            fromJson("[\"a\", true, \"false\", 1, [0]]")
        );
    }
}
