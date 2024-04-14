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
    private final JsonArrayAdapter<JE, JA, JO> jArray;
    private final JsonObjectAdapter<JE, JA, JO> jObject;

    public JsonPointer(JsonAdapter<JE, JA, JO> adapter) {
        this.adapter = adapter;
        this.jArray = adapter.jArray;
        this.jObject = adapter.jObject;
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
            if (jObject.is(obj) && jObject.has((JO)obj, token)) {
                obj = jObject.get((JO)obj, token);
            } else {
                int tokenIndex;
                try {
                    tokenIndex = Integer.parseUnsignedInt(token);
                } catch (NumberFormatException ignored) {
                    return null;
                }
                if (jArray.is(obj) && jArray.size((JA)obj) > tokenIndex) {
                    obj = jArray.get((JA)obj, tokenIndex);
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
            if (Objects.equals(tok, "-") && jArray.is(obj)) {
                tok = String.valueOf(jArray.size((JA)obj));
            }
            nextTok = refTokens.get(i + 1);

            if (jObject.is(obj)) {
                var jo = (JO)obj;
                if (!jObject.has(jo, tok)) {
                    obj = IndexPattern.matcher(nextTok).matches() ? (JE) jArray.create() : jObject.create();
                    jObject.add(jo, tok, obj);
                } else {
                    obj = jObject.get(jo, tok);
                }
            } else if (jArray.is(obj)) {
                var ja = (JA)obj;
                int intTok = Integer.parseUnsignedInt(tok);
                if (jArray.size(ja) <= intTok) {
                    obj = IndexPattern.matcher(nextTok).matches() ? (JE) jArray.create() : jObject.create();
                    while (jArray.size(ja) <= intTok) {
                        jArray.add(ja, adapter.jsonNull());
                    }
                    jArray.set(ja, intTok, obj);
                } else {
                    obj = jArray.get(ja, intTok);
                }
            }
        }
        if (jObject.is(obj)) {
            jObject.add((JO)obj, nextTok, value);
        } else if (jArray.is(obj)) {
            var ja = (JA)obj;
            if (Objects.equals(nextTok, "-")) {
                jArray.add(ja, value);
            } else {
                var intTok = Integer.parseUnsignedInt(nextTok);
                // make sure target array is in the right size
                while (jArray.size(ja) < intTok) {
                    jArray.add(ja, adapter.jsonNull());
                }
                if (insert) {
                    jArray.add(ja, adapter.jsonNull());
                    // move over all elements starting from intTok
                    for (var j = jArray.size(ja) - 1; j > intTok; j--) {
                        jArray.set(ja, j, jArray.get(ja, j - 1));
                    }
                }
                // set the right index with the value
                jArray.set(ja, intTok, value);
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
            if (Objects.equals(tok, "-") && jArray.is(obj)) {
                tok = String.valueOf(jArray.size((JA)obj));
            }
            nextTok = refTokens.get(i + 1);

            if (jObject.is(obj)) {
                var jo = (JO)obj;
                if (!jObject.has(jo, tok)) {
                    obj = IndexPattern.matcher(nextTok).matches() ? (JE) jArray.create() : jObject.create();
                    jObject.add(jo, tok, obj);
                } else {
                    obj = jObject.get(jo, tok);
                }
            } else if (jArray.is(obj)) {
                var ja = (JA)obj;
                int intTok = Integer.parseUnsignedInt(tok);
                if (jArray.size(ja) <= intTok) {
                    obj = IndexPattern.matcher(nextTok).matches() ? (JE) jArray.create() : jObject.create();
                    while (jArray.size(ja) <= intTok) {
                        jArray.add(ja, adapter.jsonNull());
                    }
                    jArray.set(ja, intTok, obj);
                } else {
                    obj = jArray.get(ja, intTok);
                }
            }
        }
        if (jObject.is(obj)) {
            var removed = jObject.remove((JO)obj, nextTok);
            return returnDocument ? doc : removed;
        } else if (jArray.is(obj)) {
            var size = jArray.size((JA)obj);
            if (Objects.equals(nextTok, "-")) {
                if (size > 0) {
                    var removed = jArray.remove((JA)obj, size - 1);
                    return returnDocument ? doc : removed;
                }
            } else {
                var intTok = Integer.parseUnsignedInt(nextTok);
                if (size > intTok) {
                    var removed = jArray.remove((JA)obj, intTok);
                    return returnDocument ? doc : removed;
                }
            }
        }
        return null;
    }
}
