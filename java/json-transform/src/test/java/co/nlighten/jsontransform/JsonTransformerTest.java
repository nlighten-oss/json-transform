package co.nlighten.jsontransform;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

public class JsonTransformerTest extends BaseTest {
    @Test
    void testDontCopyEscaped() {
        var text = "text";
        assertTransformation(text, "\\$", "$");
        assertTransformation(text, "\\#uuid", "#uuid");
        // regex matches
        assertTransformation(text, "$0", "$0");
        assertTransformation(text, "$1", "$1");
    }

    @Test
    void testDontCopyUnrecognized() {
        assertTransformation(null, "#unknown", "#unknown");
        assertTransformation(null, "$$testunknown:#now", "$$testunknown:#now");
    }

    @Test
    void testJsonPathCopy() {
        var val = "test";
        assertTransformation(val, "$", val);
        assertTransformation(fromJson("{\"a\":\"" + val + "\"}"), "$.a", val);
        assertTransformation(fromJson("[\"" + val + "\"]"), "$[0]", val);
    }

    @Test
    void testJsonPathCopyInteger() {
        var val = 123;
        assertTransformation(val, "$", new BigDecimal(val));
    }

    @Test
    void testJsonPathCopyBoolean() {
        var val = true;
        assertTransformation(val, "$", val);
    }

    @Test
    void testJsonPathCopyString() {
        var text = "text";
        assertTransformation(text, "$", text);
    }

    @Test
    void testJsonPathCopyNull() {
        Object val = null;
        assertTransformation(val, "$", val);
    }

    @Test
    void testJsonPathCopyFromAdditionalRoot() {
        var val = "text";
        var additionalContext = new HashMap<String, Object>() {{
            put("$extra", fromJson("{ \"y\": \"text\" }"));
        }};
        assertTransformation(val, "$extra.y", adapter.wrap(val), additionalContext);

        // array
        var additionalContext2 = new HashMap<String, Object>() {{
            put("$extra", fromJson("[1,2]"));
        }};
        assertTransformation(val, "$extra[0]", adapter.wrap(1), additionalContext2);

        // unrecognized root
        assertTransformation(val, "$extra2.y", adapter.wrap("$extra2.y"), additionalContext);
    }

    @Test
    void testMacroUUID() {
        var result = transform(null, "#uuid",  null);
        Assertions.assertDoesNotThrow(() -> UUID.fromString((String)(adapter.unwrap(adapter.type.cast(result), false))));
    }

    @Test
    void testMacroNow() {
        var result = transform(null, "#now",  null);
        Assertions.assertDoesNotThrow(() -> DateTimeFormatter.ISO_INSTANT.parse((String)(adapter.unwrap(adapter.type.cast(result), false))));
    }

    @Test
    void testInputExtractorSpread() {
        var m1 = fromJson("""
{
  "a": "A",
  "b": "B"
}
""");
        var t1 = fromJson("""
{
  "*": "$",
  "a": "AA"
}
""");
        var e1 = fromJson("""
{
  "a": "AA",
  "b": "B"
}
""");
        assertTransformation(m1, t1, e1);

        // check bad case
        assertFailTransformation(m1, t1, t1);
    }

    @Test
    void testInputExtractorSpreadRemoveByHashNull() {
        assertTransformation(fromJson("""
{
  "a": "A",
  "b": "B"
}
"""), fromJson("""
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

    @Test
    void testInputExtractorSpreadArray() {
        var m1 = fromJson("""
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
        var t1 = fromJson("""
{
  "*": [ "$.X", "$.Y" ],
  "a": "AA",
  "c": "CC"
}
""");
        var e1 = fromJson("""
{
  "a": "AA",
  "b": "B",
  "c": "CC",
  "d": "D"
}
""");
        assertTransformation(m1, t1, e1);

        // check bad case
        assertFailTransformation(m1, t1, t1);
    }

    @Test
    public void testInputExtractorSpreadArray2() {
        var m1 = fromJson("""
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
        var t1 = fromJson("""
{
  "*": [ "$.X", "$.Y" ],
  "a": true
}
""");
        var e1 = fromJson("""
{
  "a": true,
  "b": 2,
  "c": "C"
}
""");
        assertTransformation(m1, t1, e1);

        // check bad case
        assertFailTransformation(m1, t1, t1);
    }

    @Test
    public void testInputExtractorTransformObjectInput() {
        assertTransformation(fromJson("""
{
  "x": "foo"
}"""), fromJson("$"), fromJson("""
{
  "x": "foo"
}"""));
    }

    @Test
    public void testInputExtractorTransformDefinitionJsonArray() {
        // Given input is an object and InputExtractor definition is an array
        var definition = adapter.jArray.create();;
        adapter.jArray.add(definition, "element1");
        adapter.jArray.add(definition, 1.23);
        adapter.jArray.add(definition, false);
        adapter.jArray.add(definition, 'c');
        adapter.jArray.add(definition, adapter.jsonNull());
        var nestedJson = adapter.jObject.create();
        adapter.jObject.add(nestedJson, "nested", "*");
        adapter.jArray.add(definition, nestedJson);

        assertTransformation(fromJson("""
{
  "x": "foo"
}"""), definition, definition);
    }
}
