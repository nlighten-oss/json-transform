package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import co.nlighten.jsontransform.adapters.gson.GsonJsonTransformer;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TransformerFunctionJoinTest extends BaseTest {
    @Test
    void object() {
        var arr = new String[] { "a", "b", "c"};
        assertTransformation(arr, fromJson("""
{
  "$$join": "$"
}
"""), "abc");
        assertTransformation(arr, fromJson("""
{
  "$$join": ["$[0]", "B"]
}
"""), "aB");
        assertTransformation(arr, fromJson("""
{
  "$$join": ["$[0]", null, null, "B"],
  "delimiter": ","
}
"""), "a,B");
        assertTransformation(arr, fromJson("""
{
  "$$join": "$",
  "$$delimiter": ","
}
"""), "a,b,c");
    }

    @Test
    void object_prefix_suffix() {
        var arr = new String[]{ "hello", "world"};
        assertTransformation(arr, fromJson("""
{
  "$$join": "$",
  "delimiter": " ",
  "prefix": "<"
}
"""), "<" + String.join(" ", arr));
        assertTransformation(arr, fromJson("""
{
  "$$join": "$",
  "delimiter": " ",
  "prefix": "<",
  "suffix": ">"
}
"""), "<" + String.join(" ", arr) + ">");
    }

    @Test
    void inline() {
        var list = List.of("hello", " ", "world");
        assertTransformation(list, "$$join:$", String.join("", list) );
        assertTransformation(list, "$$join():$", String.join("", list) );
        var list2 = List.of("hello", 5, true);
        assertTransformation(list2, "$$join:$", String.join("", list2.stream().map(String::valueOf).toList()) );
        var arr = new String[]{ "hello", " ", "world"};
        assertTransformation(arr, "$$join:$", String.join("", arr) );
        var ja = fromJson(GsonJsonTransformer.ADAPTER.toString(arr));
        assertTransformation(ja, "$$join:$", String.join("", arr) );

        var withNulls = new String[] {"hello", null, "world" };
        assertTransformation(withNulls, "$$join(' '):$", "hello world" );
        assertTransformation(withNulls, "$$join(' ',,,true):$", "hello null world");
    }
    @Test
    void inline_delimiter() {
        var arr = new String[]{ "hello", " ", "world"};
        assertTransformation(arr, "$$join(,):$", String.join("", arr) );
        assertTransformation(arr, "$$join(:):$", String.join(":", arr) );
        assertTransformation(arr, "$$join(','):$", String.join(",", arr) );
        assertTransformation(arr, "$$join('\\''):$", String.join("'", arr) );
        assertTransformation(arr, "$$join('$[1]'):$", String.join(arr[1], arr) );
    }

    @Test
    void inline_prefix_suffix() {
        var arr = new String[]{ "hello", "world"};
        assertTransformation(arr, "$$join( ,<):$", "<" + String.join(" ", arr));
        assertTransformation(arr, "$$join( ,<,>):$", "<" + String.join(" ", arr) + ">" );
    }
}
