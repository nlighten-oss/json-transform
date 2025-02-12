package co.nlighten.jsontransform.manipulation;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JsonPathTest extends BaseTest {

    @Test
    void pathLeafNotConvertedToNull() {
        var map = fromJson("""
            {
                "x": [
                    { "a": 1 },
                    { "a": 2 }
                ]
            }""");
        var dc = adapter.getDocumentContext(map);
        var expected = adapter.createArray();
        adapter.add(expected, 1);
        adapter.add(expected, 2);
        assertEquals(expected, dc.read("$..a"));
    }

    @Test
    void suppressExceptions() {
        var arr = fromJson("""
                [
                   {
                      "name" : "john",
                      "gender" : "male"
                   },
                   {
                      "name" : "ben"
                   }
                ]""");
        var dc = adapter.getDocumentContext(arr);
        Assertions.assertNull(dc.read("$[1]['gender']"));
    }
}
