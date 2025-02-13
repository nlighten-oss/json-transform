package co.nlighten.jsontransform.manipulation;

import co.nlighten.jsontransform.MultiAdapterBaseTest;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class JsonPathTest extends MultiAdapterBaseTest {

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void pathLeafNotConvertedToNull(JsonAdapter<?,?,?> adapter) {
        var map = adapter.parse("""
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
        assertEquals(adapter, expected, dc.read("$..a"));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void suppressExceptions(JsonAdapter<?,?,?> adapter) {
        var arr = adapter.parse("""
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
