package co.nlighten.jsontransform.manipulation;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class JsonPointerTest extends BaseTest {

    JsonPointer jsonPointer = new JsonPointer(adapter);

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
        assertEquals("Hello", adapter.unwrap(jsonPointer.get(source, "/b/2/e")));
    }

    @Test
    public void get_KeyIsNumber() {
        var source = getEl();
        assertEquals("ONE", adapter.unwrap(jsonPointer.get(source, "/a/1")));
    }

    @Test
    public void get_NotExists() {
        var source = getEl();
        assertNull(jsonPointer.get(source, "/not/exists"));
    }

    @Test
    public void set_InObject() {
        var source = getEl();
        var value = adapter.wrap("World");
        jsonPointer.set(source, "/b/2/e", value);
        assertEquals(value, jsonPointer.get(source, "/b/2/e"));
    }

    @Test
    public void set_InArray() {
        var source = getEl();
        var value = adapter.wrap("e");
        jsonPointer.set(source, "/b/2", value);
        assertEquals(value, jsonPointer.get(source, "/b/2"));
    }

    @Test
    public void set_PushToArray() {
        var source = getEl();
        var value = adapter.wrap("e");
        jsonPointer.set(source, "/b/-", value);
        assertEquals(value, jsonPointer.get(source, "/b/3"));
    }

    @Test
    public void set_CreatePathOneKey() {
        var source = getEl();
        var value = adapter.wrap("TWO");
        jsonPointer.set(source, "/a/2", value);
        assertEquals(value, jsonPointer.get(source, "/a/2"));
    }

    @Test
    public void set_CreatePathComposite() {
        var source = adapter.parse("""
        {"foo": 1, "baz": [{"qux": "hello"}]}
        """);
        var value = adapter.wrap("world");
        jsonPointer.set(source, "/baz/0/foo", value);
        assertEquals(adapter.parse("""
            {"foo": 1, "baz": [{"qux": "hello", "foo": "world"}]}"""), source);
    }

    @Test
    public void set_InsertInside() {
        var source = adapter.parse("""
        ["foo","bar"]
        """);
        var value = adapter.wrap("world");
        jsonPointer.set(source, "/1", value, true);
        assertEquals(adapter.parse("""
        ["foo","world","bar"]"""), source);
    }

    @Test
    public void set_CreatePathOneKeyEmpty() {
        var source = getEl();
        var value = adapter.wrap("TWO");
        jsonPointer.set(source, "/a/", value);
        assertEquals(value, adapter.get(adapter.get(source, "a"), ""));
        assertEquals(value, jsonPointer.get(source, "/a/"));
    }

    @Test
    public void set_CreatePath() {
        var source = getEl();
        var value = adapter.wrap("e");
        jsonPointer.set(source, "/a/b/c/3/d", value);
        assertEquals(value, jsonPointer.get(source, "/a/b/c/3/d"));
    }

    @Test
    public void remove() {
        var source = getEl();
        var res = jsonPointer.remove(source, "/b/2/e", false);
        assertEquals(adapter.wrap("Hello"), res);

        var expected = getEl();
        adapter.remove(adapter.get(adapter.get(expected, "b"), 2), "e");
        assertEquals(expected, source);
    }

    @Test
    public void removeReturnDoc() {
        var source = getEl();
        var res = jsonPointer.remove(source, "/b/2/e");

        var expected = getEl();
        adapter.remove(adapter.get(adapter.get(expected, "b"), 2), "e");
        assertEquals(expected, res);
        assertEquals(expected, source);
    }
}
