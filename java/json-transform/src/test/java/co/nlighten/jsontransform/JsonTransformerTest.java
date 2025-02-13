package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

public class JsonTransformerTest extends MultiAdapterBaseTest {
    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testDontCopyEscaped(JsonAdapter<?,?,?> adapter) {
        var text = "text";
        assertTransformation(adapter, text, "\\$", "$");
        assertTransformation(adapter, text, "\\#uuid", "#uuid");
        // regex matches
        assertTransformation(adapter, text, "$0", "$0");
        assertTransformation(adapter, text, "$1", "$1");
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testDontCopyUnrecognized(JsonAdapter<?,?,?> adapter) {
        assertTransformation(adapter, null, "#unknown", "#unknown");
        assertTransformation(adapter, null, "$$testunknown:#now", "$$testunknown:#now");
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testJsonPathCopy(JsonAdapter<?,?,?> adapter) {
        var val = "test";
        assertTransformation(adapter, val, "$", val);
        assertTransformation(adapter, adapter.parse("{\"a\":\"" + val + "\"}"), "$.a", val);
        assertTransformation(adapter, adapter.parse("[\"" + val + "\"]"), "$[0]", val);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testJsonPathCopyInteger(JsonAdapter<?,?,?> adapter) {
        var val = 123;
        assertTransformation(adapter, val, "$", val);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testJsonPathCopyBoolean(JsonAdapter<?,?,?> adapter) {
        var val = true;
        assertTransformation(adapter, val, "$", val);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testJsonPathCopyString(JsonAdapter<?,?,?> adapter) {
        var text = "text";
        assertTransformation(adapter, text, "$", text);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testJsonPathCopyNull(JsonAdapter<?,?,?> adapter) {
        Object val = null;
        assertTransformation(adapter, val, "$", val);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testJsonPathCopyFromAdditionalRoot(JsonAdapter<?,?,?> adapter) {
        var val = "text";
        var additionalContext = new HashMap<String, Object>() {{
            put("$extra", adapter.parse("{ \"y\": \"text\" }"));
        }};
        assertTransformation(adapter, val, "$extra.y", adapter.wrap(val), additionalContext);

        // array
        var additionalContext2 = new HashMap<String, Object>() {{
            put("$extra", adapter.parse("[1,2]"));
        }};
        assertTransformation(adapter, val, "$extra[0]", adapter.wrap(1), additionalContext2);

        // unrecognized root
        assertTransformation(adapter, val, "$extra2.y", adapter.wrap("$extra2.y"), additionalContext);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testMacroUUID(JsonAdapter<?,?,?> adapter) {
        var result = transform(adapter, null, "#uuid",  null);
        Assertions.assertDoesNotThrow(() -> UUID.fromString((String)(adapter.unwrap(result))));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testMacroNow(JsonAdapter<?,?,?> adapter) {
        var result = transform(adapter, null, "#now",  null);
        Assertions.assertDoesNotThrow(() -> DateTimeFormatter.ISO_INSTANT.parse((String)(adapter.unwrap(result))));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testInputExtractorSpread(JsonAdapter<?,?,?> adapter) {
        var m1 = adapter.parse("""
{
  "a": "A",
  "b": "B"
}
""");
        var t1 = adapter.parse("""
{
  "*": "$",
  "a": "AA"
}
""");
        var e1 = adapter.parse("""
{
  "a": "AA",
  "b": "B"
}
""");
        assertTransformation(adapter, m1, t1, e1);

        // check bad case
        assertFailTransformation(adapter, m1, t1, t1);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testInputExtractorSpreadRemoveByHashNull(JsonAdapter<?,?,?> adapter) {
        assertTransformation(adapter, adapter.parse("""
{
  "a": "A",
  "b": "B"
}
"""), adapter.parse("""
{
  "*": "$",
  "a": "#null"
}
"""), adapter.parse("""
{
  "b": "B"
}
"""));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void testInputExtractorSpreadArray(JsonAdapter<?,?,?> adapter) {
        var m1 = adapter.parse("""
{
  "X": {
    "a": "A",
    "b": "B"
  },
  "Y": {
    "c": "C",
    "d": "D"
  }
}
""");
        var t1 = adapter.parse("""
{
  "*": [ "$.X", "$.Y" ],
  "a": "AA",
  "c": "CC"
}
""");
        var e1 = adapter.parse("""
{
  "a": "AA",
  "b": "B",
  "c": "CC",
  "d": "D"
}
""");
        assertTransformation(adapter, m1, t1, e1);

        // check bad case
        assertFailTransformation(adapter, m1, t1, t1);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    public void testInputExtractorSpreadArray2(JsonAdapter<?,?,?> adapter) {
        var m1 = adapter.parse("""
{
  "X": {
    "a": "A",
    "b": "B",
    "c": "C"
  },
  "Y": {
    "a": 1,
    "b": 2
  }
}
""");
        var t1 = adapter.parse("""
{
  "*": [ "$.X", "$.Y" ],
  "a": true
}
""");
        var e1 = adapter.parse("""
{
  "a": true,
  "b": 2,
  "c": "C"
}
""");
        assertTransformation(adapter, m1, t1, e1);

        // check bad case
        assertFailTransformation(adapter, m1, t1, t1);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    public void testInputExtractorTransformObjectInput(JsonAdapter<?,?,?> adapter) {
        assertTransformation(adapter, adapter.parse("""
{
  "x": "foo"
}"""), "$", adapter.parse("""
{
  "x": "foo"
}"""));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    public void testInputExtractorTransformDefinitionJsonArray(JsonAdapter<?,?,?> adapter) {
        // Given input is an object and InputExtractor definition is an array
        var definition = adapter.createArray();;
        adapter.add(definition, "element1");
        adapter.add(definition, 1.23);
        adapter.add(definition, false);
        adapter.add(definition, 'c');
        adapter.add(definition, adapter.jsonNull());
        var nestedJson = adapter.createObject();
        adapter.add(nestedJson, "nested", "*");
        adapter.add(definition, nestedJson);

        assertTransformation(adapter, adapter.parse("""
{
  "x": "foo"
}"""), definition, definition);
    }
}
