package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionFindTest extends BaseTest {
    @Test
    void findTheTruth() {
        assertTransformation(fromJson("""
[0, [], "a"]"""), fromJson("""
{
  "$$find": "$", "by": "##current"
}
"""), "a");

        // explicit boolean (non js style)
        assertTransformation(fromJson("""
["a", "1", "true"]"""), fromJson("""
{
  "$$find": "$", "by": "$$boolean:##current"
}
"""), "true");
    }
    @Test
    void nameThatStartsWithB() {
        assertTransformation(fromJson("""
[{"name":"alice"}, {"name":"ann"}, {"name":"Bob"}]"""), fromJson("""
{
  "$$find": "$", "by": "$$test('(?i)^b'):##current.name"
}
"""), fromJson("""
{"name":"Bob"}"""));
    }

    @Test
    void inline() {
        assertTransformation(fromJson("[0, [], \"a\"]"), "$$find(##current):$", "a");
    }
}
