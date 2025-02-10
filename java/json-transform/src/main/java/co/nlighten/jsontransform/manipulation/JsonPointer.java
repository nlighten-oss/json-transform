package co.nlighten.jsontransform.manipulation;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.adapters.JsonArrayAdapter;
import co.nlighten.jsontransform.adapters.JsonObjectAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Various JSON mutations for a generic JSON implementation based on
 * JavaScript Object Notation (JSON) Pointer (RFC-6901)
 */
public class JsonPointer {

    private static final Pattern IndexPattern = Pattern.compile("^(0|[1-9]\\d*|-)$");
    private final JsonAdapter<?, ?, ?> adapter;

    public JsonPointer(JsonAdapter<?, ?, ?> adapter) {
        this.adapter = adapter;
    }

    public static String unescape(String str) {
        return str.replace("~1", "/").replace("~0", "~");
    }

    public static List<String> parse(String pointer) {
        if (pointer == null) {
            return null; // throw?
        }
        var tokens = Arrays.stream((!pointer.isEmpty() && pointer.charAt(0) == '/' ? pointer.substring(1) : pointer)
                                           .split("/"))
                .map(JsonPointer::unescape)
                .collect(Collectors.toList());
        if (pointer.length() > 1 && pointer.charAt(pointer.length() - 1) == '/') {
            tokens.add("");
        }
        return tokens;
    }

    public Object get(Object obj, String pointer) {
        if (Objects.equals(pointer, "")) {
            return obj;
        }
        var tokens = parse(pointer);
        if (tokens == null) {
            return null; // throw?
        }
        for (String s : tokens) {
            var token = unescape(s);
            if (adapter.isJsonObject(obj) && adapter.has(obj, token)) {
                obj = adapter.get(obj, token);
            } else {
                int tokenIndex;
                try {
                    tokenIndex = Integer.parseUnsignedInt(token);
                } catch (NumberFormatException ignored) {
                    return null;
                }
                if (adapter.isJsonArray(obj) && adapter.size(obj) > tokenIndex) {
                    obj = adapter.get(obj, tokenIndex);
                } else {
                    return null;
                }
            }
        }
        return obj;
    }

    public Object set(Object obj, String pointer, Object value) {
        return set(obj, pointer, value, false);
    }

    public Object set(Object obj, String pointer, Object value, boolean insert) {
        if (Objects.equals(pointer, "")) {
            return value;
        }
        var result = obj;
        var refTokens = parse(pointer);
        if (refTokens == null) {
            throw new RuntimeException("Invalid pointer " + pointer);
        }
        if (refTokens.isEmpty()) {
            throw new RuntimeException("Can not set the root object");
        }
        var nextTok = refTokens.get(0);

        for (var i = 0; i < refTokens.size() - 1; ++i) {
            var tok = refTokens.get(i);
            if (Objects.equals(tok, "-") && adapter.isJsonArray(obj)) {
                tok = String.valueOf(adapter.size(obj));
            }
            nextTok = refTokens.get(i + 1);

            if (adapter.isJsonObject(obj)) {
                var jo = obj;
                if (!adapter.has(jo, tok)) {
                    obj = IndexPattern.matcher(nextTok).matches() ? adapter.createArray() : adapter.createObject();
                    adapter.add(jo, tok, obj);
                } else {
                    obj = adapter.get(jo, tok);
                }
            } else if (adapter.isJsonArray(obj)) {
                var ja = obj;
                int intTok = Integer.parseUnsignedInt(tok);
                if (adapter.size(ja) <= intTok) {
                    obj = IndexPattern.matcher(nextTok).matches() ? adapter.createArray() : adapter.createObject();
                    while (adapter.size(ja) <= intTok) {
                        adapter.add(ja, adapter.jsonNull());
                    }
                    adapter.set(ja, intTok, obj);
                } else {
                    obj = adapter.get(obj, intTok);
                }
            }
        }
        if (adapter.isJsonObject(obj)) {
            adapter.add(obj, nextTok, value);
        } else if (adapter.isJsonArray(obj)) {
            var ja = obj; // rename
            if (Objects.equals(nextTok, "-")) {
                adapter.add(ja, value);
            } else {
                var intTok = Integer.parseUnsignedInt(nextTok);
                // make sure target array is in the right size
                while (adapter.size(ja) < intTok) {
                    adapter.add(ja, adapter.jsonNull());
                }
                if (insert) {
                    adapter.add(ja, adapter.jsonNull());
                    // move over all elements starting from intTok
                    for (var j = adapter.size(ja) - 1; j > intTok; j--) {
                        adapter.set(ja, j, adapter.get(ja, j - 1));
                    }
                }
                // set the right index with the value
                adapter.set(ja, intTok, value);
            }
        }
        return result;
    }


    /**
     * @return The original object after removal.
     */
    public Object remove(Object obj, String pointer) {
        return remove(obj, pointer, true);
    }

    /**
     * @return If `returnDocument` is true, the original object after removal, otherwise, the element removed.
     */
    public Object remove(Object obj, String pointer, boolean returnDocument) {
        var doc = obj;
        var refTokens = parse(pointer);
        if (refTokens == null) {
            throw new RuntimeException("Invalid pointer " + pointer);
        }
        if (refTokens.isEmpty()) {
            throw new RuntimeException("Can not set the root object");
        }
        var nextTok = refTokens.get(0);

        for (var i = 0; i < refTokens.size() - 1; ++i) {
            var tok = refTokens.get(i);
            if (Objects.equals(tok, "-") && adapter.isJsonArray(obj)) {
                tok = String.valueOf(adapter.size(obj));
            }
            nextTok = refTokens.get(i + 1);

            if (adapter.isJsonObject(obj)) {
                var jo = obj;
                if (!adapter.has(jo, tok)) {
                    obj = IndexPattern.matcher(nextTok).matches() ? adapter.createArray() : adapter.createObject();
                    adapter.add(jo, tok, obj);
                } else {
                    obj = adapter.get(jo, tok);
                }
            } else if (adapter.isJsonArray(obj)) {
                var ja = obj;
                int intTok = Integer.parseUnsignedInt(tok);
                if (adapter.size(ja) <= intTok) {
                    obj = IndexPattern.matcher(nextTok).matches() ? adapter.createArray() : adapter.createObject();
                    while (adapter.size(ja) <= intTok) {
                        adapter.add(ja, adapter.jsonNull());
                    }
                    adapter.set(ja, intTok, obj);
                } else {
                    obj = adapter.get(ja, intTok);
                }
            }
        }
        if (adapter.isJsonObject(obj)) {
            var removed = adapter.remove(obj, nextTok);
            return returnDocument ? doc : removed;
        } else if (adapter.isJsonArray(obj)) {
            var size = adapter.size(obj);
            if (Objects.equals(nextTok, "-")) {
                if (size > 0) {
                    var removed = adapter.remove(obj, size - 1);
                    return returnDocument ? doc : removed;
                }
            } else {
                var intTok = Integer.parseUnsignedInt(nextTok);
                if (size > intTok) {
                    var removed = adapter.remove(obj, intTok);
                    return returnDocument ? doc : removed;
                }
            }
        }
        return null;
    }
}
