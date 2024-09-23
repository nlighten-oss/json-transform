package co.nlighten.jsontransform.manipulation;

import co.nlighten.jsontransform.adapters.gson.GsonJsonAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class JsonPathTest {

    // Test specific configuration for jayway implementation

    private GsonJsonAdapter adapter = new GsonJsonAdapter();
    private Gson gson = new Gson();

    @Test
    void pathLeafNotConvertedToNull() {
        var map = gson.fromJson("""
            {
                "x": [
                    { "a": 1 },
                    { "a": 2 }
                ]
            }""", Map.class);
        var dc = adapter.getDocumentContext(map);
        var expected = new JsonArray();
        expected.add(1);
        expected.add(2);
        Assertions.assertEquals(expected, dc.read("$..a"));
    }

    @Test
    void suppressExceptions() {
        var arr = gson.fromJson("""
                [
                   {
                      "name" : "john",
                      "gender" : "male"
                   },
                   {
                      "name" : "ben"
                   }
                ]""", JsonArray.class);
        var dc = adapter.getDocumentContext(arr);
        Assertions.assertNull(dc.read("$[1]['gender']"));
    }
}
