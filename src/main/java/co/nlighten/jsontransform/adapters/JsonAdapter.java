package co.nlighten.jsontransform.adapters;

import co.nlighten.jsontransform.ParameterResolver;
import com.google.gson.JsonPrimitive;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class JsonAdapter<JE, JA extends Iterable<JE>, JO extends JE> {

    private static final String JSONPATH_ROOT = "$";
    private static final String JSONPATH_ROOT_ESC = "\\$";
    private static final Character JSONPATH_ROOT_CHAR = JSONPATH_ROOT.charAt(0);
    private static final String JSONPATH_ALT_PREFIX = "#";
    private static final String JSONPATH_ALT_PREFIX_ESC = "\\#";

    public final JsonObjectAdapter<JE, JA, JO> OBJECT;
    public final JsonArrayAdapter<JE, JA, JO> ARRAY;
    public final Class<JE> type;

    public JsonAdapter(
            Class<JE> jsonElementType,
            Function<JsonAdapter<JE, JA, JO>, JsonObjectAdapter<JE, JA, JO>> objectAdapterSupplier,
            Function<JsonAdapter<JE, JA, JO>, JsonArrayAdapter<JE, JA, JO>> arrayAdapterSupplier) {
        this.OBJECT = objectAdapterSupplier.apply(this);
        this.ARRAY = arrayAdapterSupplier.apply(this);
        this.type = jsonElementType;
    }

    public abstract boolean is(Object value);
    public abstract boolean isJsonString(Object value);
    public abstract boolean isJsonNumber(Object value);
    public abstract boolean isJsonBoolean(Object value);

    public abstract boolean isNull(Object value);
    public abstract JE jsonNull();
    public abstract JE wrap(Object value);
    public abstract Object unwrap(JE value, boolean reduceBigDecimals);
    public abstract JE parse(String jsonString);

    public abstract JE clone(JE value);

    private static boolean isMathematicalInteger(double x) {
        return !Double.isNaN(x) && !Double.isInfinite(x) && x == Math.rint(x);
    }

    /**
     * Returns the string representation value of a given object
     *
     * @param value the object to extract a string value form
     * @return a string value or null if the object is null. for a complex object, a JSON string is returned
     */
    public String getAsString(Object value) {
        var object = is(value) ? unwrap((JE)value, false) : value;
        if (object == null) {
            return null;
        }
        if (object instanceof String s) {
            return s;
        }
        if (object instanceof BigDecimal bd) {
            return bd.stripTrailingZeros().toPlainString();
        }
        if (object instanceof Number n) {
            var d = n.doubleValue();
            return isMathematicalInteger(d) ? String.format("%.0f", d) : Double.toString(d);
        }
        if (object instanceof Boolean b) {
            return Boolean.toString(b);
        }
        return toString(value);
    }

    public abstract Number getNumber(JE value);
    public abstract BigDecimal getNumberAsBigDecimal(JE value);
    public abstract Boolean getBoolean(JE value);

    @SuppressWarnings("unchecked")
    public Integer compareTo(JE a, JE b) {
        if (ARRAY.is(a) && ARRAY.is(b)) {
            return Integer.compare(ARRAY.size((JA)a), ARRAY.size((JA)b));
        } else if (OBJECT.is(a) && OBJECT.is(b)) {
            return Integer.compare(OBJECT.size((JO)a), OBJECT.size((JO)b));
        } else if (isJsonString(a) && isJsonString(b)) {
            return getAsString(a).compareTo(getAsString(b));
        } else if (isJsonNumber(a) && isJsonNumber(b)) {
            return getNumberAsBigDecimal(a).compareTo(getNumberAsBigDecimal(b));
        } else if (isJsonBoolean(a) && isJsonBoolean(b)) {
            return Boolean.compare(getBoolean(a), getBoolean(b));
        } else if (isNull(a) && !isNull(b)) {
            return -1;
        } else if (!isNull(a) && isNull(b)) {
            return 1;
        }
        // incomparable
        return null;
    }

    public Comparator<JE> comparator() {
        return (JE a, JE b) -> {
            var result = compareTo(a, b);
            if (result == null) return 0; // can't compare
            return result;
        };
    }

    public boolean isTruthy(Object value) {
        return isTruthy(value, true);
    }
    public boolean isTruthy(Object value, boolean javascriptStyle) {
        if (ARRAY.is(value)) {
            return !ARRAY.isEmpty((JA)value);
        } else if (OBJECT.is(value)) {
            return !OBJECT.isEmpty((JO)value);
        }
        if (is(value)) {
            value = unwrap((JE)value, false);
        }
        if (value instanceof Boolean b) {
            return b;
        } else if (value instanceof BigDecimal) {
            return !BigDecimal.ZERO.equals(value);
        } else if (value instanceof Integer i) {
            return i != 0;
        } else if (value instanceof Long l) {
            return l != 0L;
        } else if (value instanceof Number n) {
            return n.doubleValue() != 0;
        } else if (value instanceof String s) {
            return javascriptStyle ? !s.isEmpty() : Boolean.parseBoolean(s);
        } else if (value instanceof Map<?,?> m) {
            return !m.isEmpty();
        } else if (value instanceof Array a) {
            return Array.getLength(a) != 0;
        } else if (value instanceof Iterable<?> i) {
            if (i instanceof Collection) {
                return !((Collection<?>) i).isEmpty();
            } else {
                return i.iterator().hasNext();
            }
        } else {
            return !isNull(value);
        }
    }

    public abstract void setupJsonPath();

    public DocumentContext getDocumentContext(Object payload) {
        setupJsonPath();
        Object document = payload;
        if (!is(payload)) {
            document = wrap(payload);
        }
        return JsonPath.parse(document);
    }

    public abstract String toString(Object value);

    /**
     * Builds a deque list of paths from a jsonPath string value (including map keys selectors)
     *
     * @param jsonPath the json path to build from
     * @return a dequeue with a list of element keys
     */
    static Deque<String> extractPath(String jsonPath) {
        var paths = new ArrayDeque<String>();
        if (jsonPath == null || jsonPath.isBlank()) {
            return paths;
        }
        StringBuilder sb = new StringBuilder();
        var expecting = new ArrayDeque<Character>();

        for (int i = 0; i < jsonPath.length(); i++) {
            char c = jsonPath.charAt(i);
            if (c == '.' && expecting.size() == 0 && sb.length() > 0) {
                paths.add(sb.toString());
                sb.delete(0, sb.length());
            } else if (c == '[' && expecting.size() == 0) {
                expecting.push(']');
            } else if (expecting.size() > 0 && expecting.peek() == c) {
                expecting.pollFirst();
            } else if (c == '\'' || c == '"') {
                expecting.push(c);
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) {
            paths.add(sb.toString());
        }

        return paths;
    }

    /**
     * Merges the given value object deep into the given root object at the path specified creating any missing path elements
     *
     * @param rootEl  Object to merge values into
     * @param value the value to merge into the root
     * @param path  the json path to merge the value at
     * @return the updated root object
     */
    public JO mergeInto(JE rootEl, JE value, String path) {
        var root = (JO)rootEl;
        if (value == null || root == null) {
            return root;
        }
        var location = extractPath(path);
        String point;
        var object = root;
        while ((point = location.poll()) != null) {
            if (point.equals("$")) {
                continue;
            }
            if (location.size() == 0 && !OBJECT.is(value)) {
                if (!isNull(value)) {
                    OBJECT.add(object, point, value);
                }
                return root;
            }
            if (OBJECT.has(object, point)) {
                var current = OBJECT.get(object, point);
                if (OBJECT.is(current)) {
                    object = (JO)current;
                } else if (ARRAY.is(current)) {
                    ARRAY.add((JA)current, wrapElement(value, location));
                    return root;
                } else {
                    //we create an array and add ourselves
                    var arr = ARRAY.create();
                    ARRAY.add(arr, current);
                    ARRAY.add(arr, wrapElement(value, location));
                    OBJECT.add(object, point, arr);
                }
            } else {
                var elm = wrapElement(value, location);
                if (!isNull(elm)) {
                    OBJECT.add(object, point, elm);
                }
                return root;
            }
        }
        //we merge
        if (OBJECT.is(value)) {
            var obj = (JO)value;
            var finalObject = object;
            OBJECT.entrySet(obj).forEach(kv -> OBJECT.add(finalObject, kv.getKey(), kv.getValue()));
        }

        return root;
    }

    /**
     * Builds a missing paths parent elements. e.g for a path of a.b.c the result would be {@code {a:{b:{c:value}}}}
     *
     * @param value    the value to add at the end of the path
     * @param location the location to build
     * @return a wrapping element containing the value in it's path
     */
    private JE wrapElement(JE value, Deque<String> location) {
        String point;
        var elm = value;

        while ((point = location.pollLast()) != null) {
            var obj = OBJECT.create();
            OBJECT.add(obj, point, elm);
            elm = obj;
        }
        return elm;
    }

    private interface DocumentContextSupplier extends Supplier<DocumentContext> {}

    private static String getRootFromPath(String path) {
        var indexOfDot = path.indexOf('.');
        var indexOfIndexer = path.indexOf('[');
        var endOfKeyIndex = indexOfDot > -1 && indexOfIndexer > -1 ? Math.min(indexOfDot, indexOfIndexer) : indexOfDot > -1 ? indexOfDot : indexOfIndexer;
        return endOfKeyIndex < 0 ? path : path.substring(0, endOfKeyIndex);
    }

    /**
     * Creates a parameter resolver for input using JsonPath ($...), internal macros (#null,...)
     * and additional roots (^($|#)\w+...)
     * @param payload payload to use as input
     * @param additionalContext additional documents inputs for the resolver to look in (key must start with $ or #)
     * @param unwrap unwrap JsonPrimitive values, otherwise, leave result of resolver as JsonElement
     * @return The result parameter resolver
     */
    public ParameterResolver createPayloadResolver(Object payload, Map<String, Object> additionalContext, boolean unwrap) {
        DocumentContext json = getDocumentContext(payload);
        Map<String, Object /* DocumentContext | DocumentContextSupplier | JsonElement */> additionalJsons =
                additionalContext != null && additionalContext.size() > 0
                ? additionalContext.entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> {
                                    var val = e.getValue();
                                    if (val instanceof JsonPrimitive) {
                                        return val;
                                    }
                                    if (val instanceof String || val instanceof Character || val instanceof Number || val instanceof Boolean) {
                                        return wrap(val);
                                    }
                                    return (DocumentContextSupplier)(() -> getDocumentContext(val));
                                }))
                : Collections.emptyMap();

        return name -> {
            if (name.isBlank()) return name;
            if (!name.startsWith(JSONPATH_ROOT) && !name.startsWith(JSONPATH_ALT_PREFIX)) {
                if (name.startsWith(JSONPATH_ROOT_ESC) || name.startsWith(JSONPATH_ALT_PREFIX_ESC)) {
                    name = name.substring(1);
                }
                return name;
            }
            if (name.startsWith(JSONPATH_ALT_PREFIX) && name.length() <= 5) {
                switch (name.toLowerCase()) {
                    case "#uuid" -> {
                        return UUID.randomUUID().toString();
                    }
                    case "#null" -> {
                        return null;
                    }
                    case "#now" -> {
                        return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
                    }
                }
            }
            if (name.length() < 2 ||
                    // fix for regex patterns being detected (e.g. $0)
                    (name.charAt(1) != JSONPATH_ROOT_CHAR && !Character.isDigit(name.charAt(1)))) {
                var nameKey = getRootFromPath(name);
                Object res;
                if (additionalJsons.containsKey(nameKey)) {
                    var add = additionalJsons.get(nameKey);
                    if (add instanceof DocumentContextSupplier dcs) {
                        // lazy evaluate document context until used for the first time
                        add = dcs.get();
                        additionalJsons.put(nameKey, add);
                    }
                    if (add instanceof DocumentContext dc) {
                        res = dc.read(JSONPATH_ROOT + name.substring(nameKey.length()));
                    } else {
                        res = add;
                    }
                } else if (!nameKey.equals(JSONPATH_ROOT)) {
                    return name; // unrecognized root
                } else {
                    res = json.read(name);
                }
                if (unwrap) {
                    res = unwrap(type.cast(res), true);
                }
                return res;
            }
            return name;
        };
    }
}
