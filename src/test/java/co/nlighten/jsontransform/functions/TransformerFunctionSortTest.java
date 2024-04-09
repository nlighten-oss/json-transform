package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TransformerFunctionSortTest extends BaseTest {
    @Test
    void plainSort() {
        // tests auto-detection of type
        var items = List.of("c", "a", "b");
        assertTransformation(items, fromJson("""
{
  "$$sort": "$"
}
"""), List.of("a", "b", "c"));
        var itemsNum = List.of(3, 1, 2);
        assertTransformation(itemsNum, fromJson("""
{
  "$$sort": "$"
}
"""), fromJson("[1, 2, 3]"));

        // explicit order
        assertTransformation(items, fromJson("""
{
  "$$sort": "$", "order": "desc"
}
"""), List.of("c", "b", "a"));
        assertTransformation(fromJson("""
[["c","a","b"], null, "desc"]"""), fromJson("""
{
  "$$sort": "$[0]", "order":"$[2]"
}
"""), List.of("c", "b", "a"));

        // explicit type
        assertTransformation(List.of(4,2,1,3), fromJson("""
{
  "$$sort": "$", "type": "number"
}
"""), fromJson("[1,2,3,4]"));
        assertTransformation(List.of(4,2,1,3), fromJson("""
{
  "$$sort": "$", "type": "number", "order": "desc"
}
"""), fromJson("[4,3,2,1]"));
        assertTransformation(List.of(false,false,true), fromJson("""
{
  "$$sort": "$", "type": "boolean"
}
"""), List.of(false, false, true));
        assertTransformation(List.of(false,false,true), fromJson("""
{
  "$$sort": "$", "type": "boolean", "order": "desc"
}
"""), List.of(true, false, false));
        var unsortedObjects = fromJson("""
[
 { "name": "Dan" },
 { "name": "Alice" },
 { "name": "Carl" },
 { "name": "Bob" }
]""");
        assertTransformation(unsortedObjects, fromJson("""
{
  "$$sort": "$", "by": "##current.name"
}
"""), fromJson("""
[
 { "name": "Alice" },
 { "name": "Bob" },
 { "name": "Carl" },
 { "name": "Dan" }
]"""));

        assertTransformation(unsortedObjects, fromJson("""
{
  "$$sort": "$", "type": "string", "order": "desc", "by": "##current.name"
}
"""), fromJson("""
[
 { "name": "Dan" },
 { "name": "Carl" },
 { "name": "Bob" },
 { "name": "Alice" }
]"""));
    }

    @Test
    void inlinePlainSort() {
        // tests auto-detection of type
        var items = List.of("c", "a", "b");
        assertTransformation(items, "$$sort:$", List.of("a", "b", "c"));
        var itemsNum = List.of(3, 1, 2);
        assertTransformation(itemsNum, "$$sort:$", fromJson("[1, 2, 3]"));

        // explicit order
        assertTransformation(items, "$$sort(##current,DESC):$", List.of("c", "b", "a"));
        assertTransformation(fromJson("""
[["c","a","b"], null, "desc"]"""), "$$sort(##current,$[2]):$[0]", List.of("c", "b", "a"));

        // explicit type
        assertTransformation(List.of(4,2,1,3), "$$sort(##current,ASC,NUMBER):$", fromJson("[1,2,3,4]"));
        assertTransformation(List.of(4,2,1,3), "$$sort(##current,DESC,NUMBER):$", fromJson("[4,3,2,1]"));
        assertTransformation(List.of(false,false,true), "$$sort(##current,ASC,BOOLEAN):$", List.of(false, false, true));
        assertTransformation(List.of(false,false,true), "$$sort(##current,DESC,BOOLEAN):$", List.of(true, false, false));

        var unsortedObjects = fromJson("""
[
 { "name": "Dan" },
 { "name": "Alice" },
 { "name": "Carl" },
 { "name": "Bob" }
]""");
        assertTransformation(unsortedObjects, "$$sort(##current.name):$", fromJson("""
[
 { "name": "Alice" },
 { "name": "Bob" },
 { "name": "Carl" },
 { "name": "Dan" }
]"""));

        assertTransformation(unsortedObjects, "$$sort(##current.name,DESC,STRING):$", fromJson("""
[
 { "name": "Dan" },
 { "name": "Carl" },
 { "name": "Bob" },
 { "name": "Alice" }
]"""));
    }

    @Test
    void chainedSort() {
        // tests auto-detection of type
        var items = List.of("A2", "B3", "C2", "D4", "B1", "B2", "D1");

        assertTransformation(items, fromJson("""
{
  "$$sort": "$",
  "by": "$$substring(0,1):##current"
}
"""), fromJson("""
["A2", "B3", "B1", "B2", "C2", "D4", "D1"]"""));

        assertTransformation(items, fromJson("""
{
  "$$sort": "$",
  "by": "$$substring(0,1):##current",
  "then": [
    { "by": "$$long:$$substring(-1):##current" }
  ]
}
"""), fromJson("""
["A2", "B1", "B2", "B3", "C2", "D1", "D4"]"""));

        assertTransformation(items, fromJson("""
{
  "$$sort": "$",
  "by": "$$substring(0,1):##current",
  "then": [
    { "by": "$$long:$$substring(-1):##current", "order": "DESC" }
  ]
}
"""), fromJson("""
["A2", "B3", "B2", "B1", "C2", "D4", "D1"]"""));

        assertTransformation(items, fromJson("""
{
  "$$sort": "$",
  "by": "$$substring(0,1):##current",
  "order":"DESC",
  "then": [
    { "by": "$$long:$$substring(-1):##current", "order": "ASC" }
  ]
}
"""), fromJson("""
["D1", "D4", "C2", "B1", "B2", "B3", "A2"]"""));

        assertTransformation(items, fromJson("""
{
  "$$sort": "$",
  "by": "$$substring(0,1):##current",
  "order":"DESC",
  "then": [
    { "by": "$$long:$$substring(-1):##current", "order": "DESC" }
  ]
}
"""), fromJson("""
["D4", "D1", "C2", "B3", "B2", "B1", "A2"]"""));
    }
}
