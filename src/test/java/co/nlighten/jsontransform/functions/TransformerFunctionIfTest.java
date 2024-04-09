package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionIfTest extends BaseTest {
    @Test
    void arrayForm() {
        assertTransformation(null, fromJson("""
{
  "$$if": [true, "b", "c"]
}
"""), "b");
        assertTransformation(null, fromJson("""
{
  "$$if": [true, "b"]
}
"""), "b");
        assertTransformation(null, fromJson("""
{
  "$$if": [false, "b"]
}
"""), null);
        assertTransformation(null, fromJson("""
{
  "$$if": [null, "b"]
}
"""), null);

        var arr = new String[] { "a", "b", "c"};
        // $[0] && $[1] -> "d"
        assertTransformation(arr, fromJson("""
{
  "$$if": [
    "$[0]",
    {
      "$$if": ["$[1]", "d"]
    }
  ]
}
"""), "d");

        // $[0] || $[1] || $[2] -> "d"
        assertTransformation(new Object[] { false, 0, "true" }, fromJson("""
{
  "$$if": [
    "$[0]", "d",
    { "$$if": ["$[1]", "d",
      { "$$if": ["$[2]", "d"] }
    ] }
  ]
}
"""), "d");

        var arr2 = new String[] { "a", "b", "c"};
        assertTransformation(arr2, fromJson("""
{
  "$$if": ["$[0]","$[1]","$[2]"]
}
"""), arr2[1]);

        var arr3 = new String[] { null, "b", "c"};
        assertTransformation(arr3, fromJson("""
{
  "$$if": ["$[0]","$[1]","$[2]"]
}
"""), arr3[2]);

        var arr4 = new String[] { "false", "b", "c"};
        assertTransformation(arr4, fromJson("""
{
  "$$if": ["$$boolean:$[0]","$[1]","$[2]"]
}
"""), arr4[2]);


        // invalid input - treat as object
        assertTransformation(new Boolean[] { true }, fromJson("{\"$$if\":\"$\"}"), null);
        assertTransformation("hello", fromJson("{\"$$if\":\"$\"}"), null);
    }

    @Test
    void objectForm() {
        assertTransformation(null, fromJson("""
{
  "$$if": true, "then": "b", "else": "c"
}
"""), "b");
        assertTransformation(null, fromJson("""
{
  "$$if": true, "then": "b"
}
"""), "b");
        assertTransformation(null, fromJson("""
{
  "$$if": false, "then": "b"
}
"""), null);
        assertTransformation(null, fromJson("""
{
  "$$if": "$", "then": "b"
}
"""), null);

        var arr = new String[] { "a", "b", "c"};
        // $[0] && $[1] -> "d"
        assertTransformation(arr, fromJson("""
{
  "$$if": "$[0]", "then": { "$$if": "$[1]", "then": "d" }
}
"""), "d");

        // $[0] || $[1] || $[2] -> "d"
        assertTransformation(new Object[] { false, 0, "true" }, fromJson("""
{
  "$$if": "$[0]", 
  "then": "d",
  "else": { 
    "$$if": "$[1]", 
    "then": "d",
    "else": { 
        "$$if": "$[2]", 
        "then": "d"
    }
  }
}
"""), "d");

        var arr2 = new String[] { "a", "b", "c"};
        assertTransformation(arr2, fromJson("""
{
  "$$if": "$[0]", "then": "$[1]", "else": "$[2]"
}
"""), arr2[1]);

        var arr3 = new String[] { null, "b", "c"};
        assertTransformation(arr3, fromJson("""
{
  "$$if": "$[0]", "then": "$[1]", "else": "$[2]"
}
"""), arr3[2]);

        var arr4 = new String[] { "false", "b", "c"};
        assertTransformation(arr4, fromJson("""
{
  "$$if": "$$boolean:$[0]", "then": "$[1]", "else": "$[2]"
}
"""), arr4[2]);
    }

    @Test
    void inline() {
        assertTransformation(true, "$$if(a,b):$", "a");
        assertTransformation(false, "$$if(a,b):$", "b");
        assertTransformation(false, "$$if(a):$", null);
    }

}
