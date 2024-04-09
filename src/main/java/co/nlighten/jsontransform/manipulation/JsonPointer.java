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
public class JsonPointer<JE, JA extends Iterable<JE>, JO extends JE> {

    private static final Pattern IndexPattern = Pattern.compile("^(0|[1-9]\\d*|-)$");
    private final JsonAdapter<JE, JA, JO> adapter;
    private final JsonArrayAdapter<JE, JA, JO> ARRAY;
    private final JsonObjectAdapter<JE, JA, JO> OBJECT;

    public JsonPointer(JsonAdapter<JE, JA, JO> adapter) {
        this.adapter = adapter;
        this.ARRAY = adapter.ARRAY;
        this.OBJECT = adapter.OBJECT;
    }

    public static String unescape(String str) {
        return str.replace("~1", "/").replace("~0", "~");
    }

    public static List<String> parse(String pointer) {
        if (pointer == null) {
            return null; // throw?
        }
        var tokens = Arrays.stream((pointer.length() > 0 && pointer.charAt(0) == '/' ? pointer.substring(1) : pointer)
                                           .split("/"))
                .map(JsonPointer::unescape)
                .collect(Collectors.toList());
        if (pointer.length() > 1 && pointer.charAt(pointer.length() - 1) == '/') {
            tokens.add("");
        }
        return tokens;
    }

    public JE get(JE obj, String pointer) {
        if (Objects.equals(pointer, "")) {
            return obj;
        }
        var tokens = parse(pointer);
        if (tokens == null) {
            return null; // throw?
        }
        for (String s : tokens) {
            var token = unescape(s);
            if (OBJECT.is(obj) && OBJECT.has((JO)obj, token)) {
                obj = OBJECT.get((JO)obj, token);
            } else {
                int tokenIndex;
                try {
                    tokenIndex = Integer.parseUnsignedInt(token);
                } catch (NumberFormatException ignored) {
                    return null;
                }
                if (ARRAY.is(obj) && ARRAY.size((JA)obj) > tokenIndex) {
                    obj = ARRAY.get((JA)obj, tokenIndex);
                } else {
                    return null;
                }
            }
        }
        return obj;
    }

    public JE set(JE obj, String pointer, JE value) {
        return set(obj, pointer, value, false);
    }

    public JE set(JE obj, String pointer, JE value, boolean insert) {
        if (Objects.equals(pointer, "")) {
            return value;
        }
        var result = obj;
        var refTokens = parse(pointer);
        if (refTokens == null) {
            throw new RuntimeException("Invalid pointer " + pointer);
        }
        if (refTokens.size() == 0) {
            throw new RuntimeException("Can not set the root object");
        }
        var nextTok = refTokens.get(0);

        for (var i = 0; i < refTokens.size() - 1; ++i) {
            var tok = refTokens.get(i);
            if (Objects.equals(tok, "-") && ARRAY.is(obj)) {
                tok = String.valueOf(ARRAY.size((JA)obj));
            }
            nextTok = refTokens.get(i + 1);

            if (OBJECT.is(obj)) {
                var jo = (JO)obj;
                if (!OBJECT.has(jo, tok)) {
                    obj = IndexPattern.matcher(nextTok).matches() ? (JE)ARRAY.create() : OBJECT.create();
                    OBJECT.add(jo, tok, obj);
                } else {
                    obj = OBJECT.get(jo, tok);
                }
            } else if (ARRAY.is(obj)) {
                var ja = (JA)obj;
                int intTok = Integer.parseUnsignedInt(tok);
                if (ARRAY.size(ja) <= intTok) {
                    obj = IndexPattern.matcher(nextTok).matches() ? (JE)ARRAY.create() : OBJECT.create();
                    while (ARRAY.size(ja) <= intTok) {
                        ARRAY.add(ja, adapter.jsonNull());
                    }
                    ARRAY.set(ja, intTok, obj);
                } else {
                    obj = ARRAY.get(ja, intTok);
                }
            }
        }
        if (OBJECT.is(obj)) {
            OBJECT.add((JO)obj, nextTok, value);
        } else if (ARRAY.is(obj)) {
            var ja = (JA)obj;
            if (Objects.equals(nextTok, "-")) {
                ARRAY.add(ja, value);
            } else {
                var intTok = Integer.parseUnsignedInt(nextTok);
                // make sure target array is in the right size
                while (ARRAY.size(ja) < intTok) {
                    ARRAY.add(ja, adapter.jsonNull());
                }
                if (insert) {
                    ARRAY.add(ja, adapter.jsonNull());
                    // move over all elements starting from intTok
                    for (var j = ARRAY.size(ja) - 1; j > intTok; j--) {
                        ARRAY.set(ja, j, ARRAY.get(ja, j - 1));
                    }
                }
                // set the right index with the value
                ARRAY.set(ja, intTok, value);
            }
        }
        return result;
    }


    /**
     * @return The original object after removal.
     */
    public JE remove(JE obj, String pointer) {
        return remove(obj, pointer, true);
    }

    /**
     * @return If `returnDocument` is true, the original object after removal, otherwise, the element removed.
     */
    public JE remove(JE obj, String pointer, boolean returnDocument) {
        var doc = obj;
        var refTokens = parse(pointer);
        if (refTokens == null) {
            throw new RuntimeException("Invalid pointer " + pointer);
        }
        if (refTokens.size() == 0) {
            throw new RuntimeException("Can not set the root object");
        }
        var nextTok = refTokens.get(0);

        for (var i = 0; i < refTokens.size() - 1; ++i) {
            var tok = refTokens.get(i);
            if (Objects.equals(tok, "-") && ARRAY.is(obj)) {
                tok = String.valueOf(ARRAY.size((JA)obj));
            }
            nextTok = refTokens.get(i + 1);

            if (OBJECT.is(obj)) {
                var jo = (JO)obj;
                if (!OBJECT.has(jo, tok)) {
                    obj = IndexPattern.matcher(nextTok).matches() ? (JE)ARRAY.create() : OBJECT.create();
                    OBJECT.add(jo, tok, obj);
                } else {
                    obj = OBJECT.get(jo, tok);
                }
            } else if (ARRAY.is(obj)) {
                var ja = (JA)obj;
                int intTok = Integer.parseUnsignedInt(tok);
                if (ARRAY.size(ja) <= intTok) {
                    obj = IndexPattern.matcher(nextTok).matches() ? (JE)ARRAY.create() : OBJECT.create();
                    while (ARRAY.size(ja) <= intTok) {
                        ARRAY.add(ja, adapter.jsonNull());
                    }
                    ARRAY.set(ja, intTok, obj);
                } else {
                    obj = ARRAY.get(ja, intTok);
                }
            }
        }
        if (OBJECT.is(obj)) {
            var removed = OBJECT.remove((JO)obj, nextTok);
            return returnDocument ? doc : removed;
        } else if (ARRAY.is(obj)) {
            var size = ARRAY.size((JA)obj);
            if (Objects.equals(nextTok, "-")) {
                if (size > 0) {
                    var removed = ARRAY.remove((JA)obj, size - 1);
                    return returnDocument ? doc : removed;
                }
            } else {
                var intTok = Integer.parseUnsignedInt(nextTok);
                if (size > intTok) {
                    var removed = ARRAY.remove((JA)obj, intTok);
                    return returnDocument ? doc : removed;
                }
            }
        }
        return null;
    }
}
