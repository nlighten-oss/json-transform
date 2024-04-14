package co.nlighten.jsontransform.manipulation;

import co.nlighten.jsontransform.BaseTest;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JsonPointerTest extends BaseTest {

    JsonPointer jsonPointer = new JsonPointer<>(adapter);

    Object getEl() {
        return adapter.parse("""
{
    "a": {
        "1": "ONE"
    },
    "b": [
        "c",
        "d",
        {
            "e": "Hello"
        }
    ]
}
""");
    }

    @Test
    public void get_Simple() {
        var source = getEl();
        assertEquals(new JsonPrimitive("Hello"), jsonPointer.get(source, "/b/2/e"));
    }

    @Test
    public void get_KeyIsNumber() {
        var source = getEl();
        assertEquals(new JsonPrimitive("ONE"), jsonPointer.get(source, "/a/1"));
    }

    @Test
    public void get_NotExists() {
        var source = getEl();
        assertNull(jsonPointer.get(source, "/not/exists"));
    }

    @Test
    public void set_InObject() {
        var source = getEl();
        var value = new JsonPrimitive("World");
        jsonPointer.set(source, "/b/2/e", value);
        assertEquals(value, jsonPointer.get(source, "/b/2/e"));
    }

    @Test
    public void set_InArray() {
        var source = getEl();
        var value = new JsonPrimitive("e");
        jsonPointer.set(source, "/b/2", value);
        assertEquals(value, jsonPointer.get(source, "/b/2"));
    }

    @Test
    public void set_PushToArray() {
        var source = getEl();
        var value = new JsonPrimitive("e");
        jsonPointer.set(source, "/b/-", value);
        assertEquals(value, jsonPointer.get(source, "/b/3"));
    }

    @Test
    public void set_CreatePathOneKey() {
        var source = getEl();
        var value = new JsonPrimitive("TWO");
        jsonPointer.set(source, "/a/2", value);
        assertEquals(value, jsonPointer.get(source, "/a/2"));
    }

    @Test
    public void set_CreatePathComposite() {
        var source = adapter.parse("""
        {"foo": 1, "baz": [{"qux": "hello"}]}
        """);
        var value = new JsonPrimitive("world");
        jsonPointer.set(source, "/baz/0/foo", value);
        assertEquals(adapter.parse("""
            {"foo": 1, "baz": [{"qux": "hello", "foo": "world"}]}"""), source);
    }

    @Test
    public void set_InsertInside() {
        var source = adapter.parse("""
        ["foo","bar"]
        """);
        var value = new JsonPrimitive("world");
        jsonPointer.set(source, "/1", value, true);
        assertEquals(adapter.parse("""
        ["foo","world","bar"]"""), source);
    }

    @Test
    public void set_CreatePathOneKeyEmpty() {
        var source = getEl();
        var value = new JsonPrimitive("TWO");
        jsonPointer.set(source, "/a/", value);
        assertEquals(value, adapter.jObject.convert(adapter.jObject.convert(source).get("a")).get(""));
        assertEquals(value, jsonPointer.get(source, "/a/"));
    }

    @Test
    public void set_CreatePath() {
        var source = getEl();
        var value = new JsonPrimitive("e");
        jsonPointer.set(source, "/a/b/c/3/d", value);
        assertEquals(value, jsonPointer.get(source, "/a/b/c/3/d"));
    }

    @Test
    public void remove() {
        var source = getEl();
        var res = jsonPointer.remove(source, "/b/2/e", false);
        assertEquals(new JsonPrimitive("Hello"), res);

        var expected = getEl();
        adapter.jObject.convert(
        adapter.jArray.type.cast(
        adapter.jObject.convert(expected)
                .get("b"))
                .get(2))
                .remove("e");
        assertEquals(expected, source);
    }

    @Test
    public void removeReturnDoc() {
        var source = getEl();
        var res = jsonPointer.remove(source, "/b/2/e");
        var expected = getEl();
        adapter.jObject.convert(
        adapter.jArray.type.cast(
        adapter.jObject.convert(expected)
                .get("b"))
                .get(2))
                .remove("e");
        assertEquals(expected, res);
        assertEquals(expected, source);
    }
}
