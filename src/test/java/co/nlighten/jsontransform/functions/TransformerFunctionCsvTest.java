package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;
public class TransformerFunctionCsvTest extends BaseTest {
    @Test
    void inline() {
        assertTransformation(fromJson("""
               [{"a":"A","b":1},{"a":"C","b":2}]"""),
             "$$csv:$",
             """
a,b
A,1
C,2
""");
        assertTransformation(fromJson("""
               [{"a":"A","b":1},{"a":"C","b":2}]"""),
             "$$csv(true):$",
             """
A,1
C,2
""");
    }

    @Test
    void object() {
        assertTransformation(
                fromJson("""
               [{"a":"A","b":1},{"a":"C","b":2}]"""),
                fromJson("""
                    {
                      "$$csv": "$"
                    }"""), """
a,b
A,1
C,2
""");
        assertTransformation(
                fromJson("""
               [{"a":"A","b":1},{"a":"C","b":2}]"""),
                fromJson("""
                    {
                      "$$csv": "$",
                      "no_headers": true
                    }"""), """
A,1
C,2
""");
   }

    @Test
    void testObject_names() {
        assertTransformation(
               fromJson("[[1,2],[3,4]]"),
               fromJson("""
                    {
                      "$$csv": "$",
                      "names": ["a","b"]
                    }"""), """
a,b
1,2
3,4
""");
        // without names
        assertTransformation(
                fromJson("[[1,2],[3,4]]"),
                fromJson("""
                    {
                      "$$csv": "$"
                    }"""), """
1,2
3,4
""");
    }
}
