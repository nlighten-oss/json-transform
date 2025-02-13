package co.nlighten.jsontransform.manipulation;

import co.nlighten.jsontransform.MultiAdapterBaseTest;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertNull;

public class JsonPointerTest extends MultiAdapterBaseTest {


    Object getEl(JsonAdapter<?, ?, ?> adapter) {
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

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    public void get_Simple(JsonAdapter<?,?,?> adapter) {
        var source = getEl(adapter);
        var jsonPointer = new JsonPointer(adapter);
        assertEquals(adapter, "Hello", adapter.unwrap(jsonPointer.get(source, "/b/2/e")));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    public void get_KeyIsNumber(JsonAdapter<?,?,?> adapter) {
        var source = getEl(adapter);
        var jsonPointer = new JsonPointer(adapter);
        assertEquals(adapter, "ONE", adapter.unwrap(jsonPointer.get(source, "/a/1")));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    public void get_NotExists(JsonAdapter<?,?,?> adapter) {
        var source = getEl(adapter);
        var jsonPointer = new JsonPointer(adapter);
        assertNull(jsonPointer.get(source, "/not/exists"));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    public void set_InObject(JsonAdapter<?,?,?> adapter) {
        var source = getEl(adapter);
        var value = adapter.wrap("World");
        var jsonPointer = new JsonPointer(adapter);
        jsonPointer.set(source, "/b/2/e", value);
        assertEquals(adapter, value, jsonPointer.get(source, "/b/2/e"));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    public void set_InArray(JsonAdapter<?,?,?> adapter) {
        var source = getEl(adapter);
        var value = adapter.wrap("e");
        var jsonPointer = new JsonPointer(adapter);
        jsonPointer.set(source, "/b/2", value);
        assertEquals(adapter, value, jsonPointer.get(source, "/b/2"));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    public void set_PushToArray(JsonAdapter<?,?,?> adapter) {
        var source = getEl(adapter);
        var value = adapter.wrap("e");
        var jsonPointer = new JsonPointer(adapter);
        jsonPointer.set(source, "/b/-", value);
        assertEquals(adapter, value, jsonPointer.get(source, "/b/3"));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    public void set_CreatePathOneKey(JsonAdapter<?,?,?> adapter) {
        var source = getEl(adapter);
        var value = adapter.wrap("TWO");
        var jsonPointer = new JsonPointer(adapter);
        jsonPointer.set(source, "/a/2", value);
        assertEquals(adapter, value, jsonPointer.get(source, "/a/2"));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    public void set_CreatePathComposite(JsonAdapter<?,?,?> adapter) {
        var source = adapter.parse("""
        {"foo": 1, "baz": [{"qux": "hello"}]}
        """);
        var value = adapter.wrap("world");
        var jsonPointer = new JsonPointer(adapter);
        jsonPointer.set(source, "/baz/0/foo", value);
        assertEquals(adapter, adapter.parse("""
            {"foo": 1, "baz": [{"qux": "hello", "foo": "world"}]}"""), source);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    public void set_InsertInside(JsonAdapter<?,?,?> adapter) {
        var source = adapter.parse("""
        ["foo","bar"]
        """);
        var value = adapter.wrap("world");
        var jsonPointer = new JsonPointer(adapter);
        jsonPointer.set(source, "/1", value, true);
        assertEquals(adapter, adapter.parse("""
        ["foo","world","bar"]"""), source);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    public void set_CreatePathOneKeyEmpty(JsonAdapter<?,?,?> adapter) {
        var source = getEl(adapter);
        var value = adapter.wrap("TWO");
        var jsonPointer = new JsonPointer(adapter);
        jsonPointer.set(source, "/a/", value);
        assertEquals(adapter, value, adapter.get(adapter.get(source, "a"), ""));
        assertEquals(adapter, value, jsonPointer.get(source, "/a/"));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    public void set_CreatePath(JsonAdapter<?,?,?> adapter) {
        var source = getEl(adapter);
        var value = adapter.wrap("e");
        var jsonPointer = new JsonPointer(adapter);
        jsonPointer.set(source, "/a/b/c/3/d", value);
        assertEquals(adapter, value, jsonPointer.get(source, "/a/b/c/3/d"));
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    public void remove(JsonAdapter<?,?,?> adapter) {
        var source = getEl(adapter);
        var jsonPointer = new JsonPointer(adapter);
        var res = jsonPointer.remove(source, "/b/2/e", false);
        assertEquals(adapter, adapter.wrap("Hello"), res);

        var expected = getEl(adapter);
        adapter.remove(adapter.get(adapter.get(expected, "b"), 2), "e");
        assertEquals(adapter, expected, source);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    public void removeReturnDoc(JsonAdapter<?,?,?> adapter) {
        var source = getEl(adapter);
        var jsonPointer = new JsonPointer(adapter);
        var res = jsonPointer.remove(source, "/b/2/e");

        var expected = getEl(adapter);
        adapter.remove(adapter.get(adapter.get(expected, "b"), 2), "e");
        assertEquals(adapter, expected, res);
        assertEquals(adapter, expected, source);
    }
}
