package co.nlighten.jsontransform.adapters;

import co.nlighten.jsontransform.ParameterResolver;
import com.google.gson.JsonPrimitive;
import com.jayway.jsonpath.*;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JsonAdapter is a base class for Json element adapters. It provides methods for working with Json elements
 * @param <JE> the Json element type
 * @param <JA> the Json array type
 * @param <JO> the Json object type
 */
public abstract class JsonAdapter<JE, JA extends Iterable<JE>, JO extends JE> {

    private static final String JSONPATH_ROOT = "$";
    private static final String JSONPATH_ROOT_ESC = "\\$";
    private static final Character JSONPATH_ROOT_CHAR = JSONPATH_ROOT.charAt(0);
    private static final String JSONPATH_ALT_PREFIX = "#";
    private static final String JSONPATH_ALT_PREFIX_ESC = "\\#";

    private final JsonObjectAdapter<JE, JA, JO> jObject;
    private final JsonArrayAdapter<JE, JA, JO> jArray;

    protected final ParseContext jsonPath;
    protected final com.jayway.jsonpath.Configuration jsonPathConfiguration;

    public JsonAdapter(
            Supplier<JsonObjectAdapter<JE, JA, JO>> objectAdapterSupplier,
            Supplier<JsonArrayAdapter<JE, JA, JO>> arrayAdapterSupplier,
            com.jayway.jsonpath.Configuration jsonPathConfiguration) {
        this.jObject = objectAdapterSupplier.get();
        this.jArray = arrayAdapterSupplier.get();
        this.jsonPathConfiguration = jsonPathConfiguration;
        this.jsonPath = JsonPath.using(jsonPathConfiguration);
    }

    /**
     * Checks if the given object is a Json element
     * @param value the object to check
     * @return true if the object is a Json element
     */
    public abstract boolean is(Object value);

    public boolean isJsonObject(Object value) {
        return jObject.is(value);
    }

    public boolean isJsonArray(Object value) {
        return jArray.is(value);
    }

    /**
     * Checks if the given object is a Json string
     * @param value the object to check
     * @return true if the object is a Json string
     */
    public abstract boolean isJsonString(Object value);

    /**
     * Checks if the given object is a Json number
     * @param value the object to check
     * @return true if the object is a Json number
     */
    public abstract boolean isJsonNumber(Object value);

    /**
     * Checks if the given object is a Json boolean
     * @param value the object to check
     * @return true if the object is a Json boolean
     */
    public abstract boolean isJsonBoolean(Object value);

    /**
     * Checks if the given object is a Json Null
     * @param value the object to check
     * @return true if the object is a Json Null
     */
    public abstract boolean isNull(Object value);

    /**
     * Creates a Json Null element
     * @return a Json Null element
     */
    public abstract JE jsonNull();

    /**
     * Wraps the given object into a Json element
     * @param value the object to wrap
     * @return a Json element
     */
    public abstract JE wrap(Object value);

    /**
     * Unwraps the given Json element into a Java object (if not wrapper, it will just be returned as-is)
     * @param value the possible Json element to unwrap
     * @param reduceBigDecimals reduce big decimals to less precise java number types
     * @return a Java object
     */
    public abstract Object unwrap(Object value, boolean reduceBigDecimals);

    /**
     * Unwraps the given Json element into a Java object (if not wrapper, it will just be returned as-is)
     * @param value the possible Json element to unwrap
     * @return a Java object
     */
    public Object unwrap(Object value) {
        return unwrap(value, false);
    }

    /**
     * Parses a Json string into a Json element
     * @param jsonString the Json string to parse
     * @return a Json element
     */
    public abstract JE parse(String jsonString);

    /**
     * Clones a Json element
     * @param value the Json element to clone
     * @return a cloned Json element
     */
    public abstract Object clone(Object value);

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
        var object = is(value) ? unwrap(value, false) : value;
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

    /**
     * Converts the given object into a Json element
     * @param value the object to convert
     * @return a Json element
     */
    public abstract Number getNumber(Object value);

    /**
     * Converts the given object into a BigDecimal
     * @param value the object to convert
     * @return a BigDecimal
     */
    public abstract BigDecimal getNumberAsBigDecimal(Object value);

    /**
     * Converts the given object into a Boolean
     * @param value the object to convert
     * @return a Boolean
     */
    public abstract Boolean getBoolean(Object value);

    /**
     * Convert the specified object to one of supported types
     * @param type type of object to extract as ("NUMBER" / "BOOLEAN" / "STRING" / "AUTO")
     * @param value the value
     * @return the extracted value
     */
    public Object getAs(String type, Object value) {
        if ("NUMBER".equals(type) || isJsonNumber(value)) {
            return getNumberAsBigDecimal(value);
        }
        if ("BOOLEAN".equals(type) || isJsonBoolean(value)) {
            return getBoolean(value);
        }
        if ("STRING".equals(type) || isJsonString(value)) {
            return getAsString(value);
        }
        // "AUTO"
        return value;
    }

    /**
     * Compares two Json elements
     * @param a the first Json element
     * @param b the second Json element
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second
     */
    @SuppressWarnings("unchecked")
    public Integer compareTo(Object a, Object b) {
        if (jArray.is(a) && jArray.is(b)) {
            return Integer.compare(jArray.size((JA)a), jArray.size((JA)b));
        } else if (jObject.is(a) && jObject.is(b)) {
            return Integer.compare(jObject.size((JO)a), jObject.size((JO)b));
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

    /**
     * Returns a comparator for Json elements
     * @return a comparator for Json elements
     */
    public Comparator<Object> comparator() {
        return (Object a, Object b) -> {
            var result = compareTo(a, b);
            if (result == null) return 0; // can't compare
            return result;
        };
    }

    /**
     * Checks if the given object is truthy
     * @param value the object to check
     * @return true if the object is truthy
     */
    public boolean isTruthy(Object value) {
        return isTruthy(value, true);
    }

    /**
     * Checks if the given object is truthy
     * @param value the object to check
     * @param javascriptStyle whether to use javascript style truthy checks
     * @return true if the object is truthy
     */
    public boolean isTruthy(Object value, boolean javascriptStyle) {
        if (jArray.is(value)) {
            return !jArray.isEmpty((JA)value);
        } else if (jObject.is(value)) {
            return !jObject.isEmpty((JO)value);
        }
        if (is(value)) {
            value = unwrap(value, false);
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

    /**
     * Creates a Json object
     * @return a Json object
     */
    public JO createObject() {
        return jObject.create();
    }

    /**
     * Checks if the given object contains the specified key.
     * @param obj the object to check
     * @param key the key to check for
     * @return true if the object contains the key
     */
    public boolean has(Object obj, String key) {
        if (!jObject.is(obj)) {
            throw new IllegalArgumentException("obj is not a Json object");
        }
        return jObject.has((JO)obj, key);
    }

    /**
     * Returns the value of the specified key in the object.
     * @param obj the object to get the value from
     * @param key the key to get the value for
     * @return the value of the key in the object
     */
    public JE get(Object obj, String key) {
        if (!jObject.is(obj)) {
            throw new IllegalArgumentException("obj is not a Json object");
        }
        return jObject.get((JO)obj, key);
    }

    /**
     * Returns a set of members key values.
     *
     * @return a set of member keys as Strings
     */
    public Set<String> keySet(Object value) {
        return jObject.keySet((JO)value);
    }

    /**
     * Returns a set of members of this object. The set is ordered, and the order is in which the
     * elements were added.
     *
     * @return a set of members of this object.
     */
    public Set<Map. Entry<String, JE>> entrySet(Object value) {
        return jObject.entrySet((JO)value);
    }


    public void add(Object obj, String key, Object value) {
        if (!jObject.is(obj)) {
            throw new IllegalArgumentException("obj is not a Json object");
        }
        if (value instanceof String s) {
            jObject.add((JO)obj, key, s);
        } else if (value instanceof Number n) {
            jObject.add((JO)obj, key, n);
        } else if (value instanceof Boolean b) {
            jObject.add((JO)obj, key, b);
        } else if (value instanceof Character c) {
            jObject.add((JO)obj, key, c);
        } else if (is(value)) {
            jObject.add((JO)obj, key, (JE)value);
        } else {
            throw new IllegalArgumentException("value is not a Json element");
        }
    }
    public JE remove(Object obj, String key) {
        if (!jObject.is(obj)) {
            throw new IllegalArgumentException("obj is not a Json object");
        }
        return jObject.remove((JO)obj, key);
    }

    public JA createArray(int capacity) {
        return jArray.create(capacity);
    }
    public JA createArray() {
        return jArray.create();
    }
    public JE get(Object arr, int index) {
        if (!jArray.is(arr)) {
            throw new IllegalArgumentException("arr is not a Json array");
        }
        return jArray.get((JA)arr, index);
    }
    public void add(Object arr, Object value) {
        if (!jArray.is(arr)) {
            throw new IllegalArgumentException("arr is not a Json array");
        }
        if (value instanceof String s) {
            jArray.add((JA)arr, s);
        } else if (value instanceof Number n) {
            jArray.add((JA)arr, n);
        } else if (value instanceof Boolean b) {
            jArray.add((JA)arr, b);
        } else if (value instanceof Character c) {
            jArray.add((JA)arr, c);
        } else if (is(value)) {
            jArray.add((JA)arr, (JE)value);
        } else {
            throw new IllegalArgumentException("value is not a Json element");
        }
    }

    /**
     * Sets the value at the given index in the array.
     * If the index is over bounds, the array will be expanded with nulls.
     */
    public void set(Object arr, int index, Object value) {
        if (!jArray.is(arr)) {
            throw new IllegalArgumentException("arr is not a Json array");
        }
        if (!is(value)) {
            throw new IllegalArgumentException("value is not a Json element");
        }
        jArray.set((JA)arr, index, (JE)value);
    }
    public JE remove(Object arr, int index) {
        if (!jArray.is(arr)) {
            throw new IllegalArgumentException("arr is not a Json array");
        }
        return jArray.remove((JA)arr, index);
    }
    public Stream<?> stream(Object arr, boolean parallel) {
        if (!jArray.is(arr)) {
            throw new IllegalArgumentException("arr is not a Json array");
        }
        return jArray.stream((JA)arr, parallel);
    }
    public Stream<?> stream(Object arr) {
        return stream(arr, false);
    }
    public Iterable<?> asIterable(Object arr) {
        if (!jArray.is(arr)) {
            throw new IllegalArgumentException("arr is not a Json array");
        }
        return (JA)arr;
    }

    public int size(Object value) {
        if (isJsonArray(value)) {
            return jArray.size((JA)value);
        }
        if (isJsonObject(value)) {
            return jObject.size((JO)value);
        }
        throw new IllegalArgumentException("value is not a Json object nor array");
    }

    public boolean isEmpty(Object value) {
        if (isJsonArray(value)) {
            return jArray.isEmpty((JA)value);
        }
        if (isJsonString(value)) {
            return getAsString(value).isEmpty();
        }
        if (isJsonObject(value)) {
            return jObject.isEmpty((JO)value);
        }
        return false;
    }

    /**
     * Returns a document context for the given payload (Using JsonPath)
     * @param payload the payload to create a document context for
     * @return a document context
     */
    public DocumentContext getDocumentContext(Object payload, Iterable<String> options) {
        Object document = payload;
        if (!is(payload)) {
            document = wrap(payload);
        }
        var parseContext = jsonPath;
        if (options != null) {
            var opts = new HashSet<Option>();
            for (var option : options) {
                opts.add(Option.valueOf(option));
            }
            parseContext = JsonPath.using(new Configuration.ConfigurationBuilder()
                    .jsonProvider(jsonPathConfiguration.jsonProvider())
                    .mappingProvider(jsonPathConfiguration.mappingProvider())
                    .options(opts)
                    .build());
        }
        return parseContext.parse(document);
    }

    public DocumentContext getDocumentContext(Object payload) {
        return getDocumentContext(payload, null);
    }

    public boolean nodesComparable() {
        return true;
    }

    /**
     * Checks if the given objects are equal based on Json implementation
     * @param value the first object
     * @param other the second object
     * @return true if the objects are equal
     */
    public boolean areEqual(Object value, Object other) {
        return Objects.equals(value, other);
    }

    /**
     * Returns the hash code of the given object
     * @param value the object to get the hash code for
     * @return the hash code of the object
     */
    public int hashCode(Object value) {
        return value == null ? 0 : value.hashCode();
    }

    /**
     * Converts the given object into a string representation
     * @param value the object to convert
     * @return a string representation of the object
     */
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
    public Object mergeInto(Object rootEl, Object value, String path) {
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
            if (location.size() == 0 && !jObject.is(value)) {
                if (!isNull(value)) {
                    jObject.add(object, point, (JE)value);
                }
                return root;
            }
            if (jObject.has(object, point)) {
                var current = jObject.get(object, point);
                if (jObject.is(current)) {
                    object = (JO)current;
                } else if (jArray.is(current)) {
                    jArray.add((JA)current, wrapElement((JE)value, location));
                    return root;
                } else {
                    //we create an array and add ourselves
                    var arr = jArray.create();
                    jArray.add(arr, current);
                    jArray.add(arr, wrapElement((JE)value, location));
                    jObject.add(object, point, arr);
                }
            } else {
                var elm = wrapElement((JE)value, location);
                if (!isNull(elm)) {
                    jObject.add(object, point, elm);
                }
                return root;
            }
        }
        //we merge
        if (jObject.is(value)) {
            var obj = (JO)value;
            var finalObject = object;
            jObject.entrySet(obj).forEach(kv -> jObject.add(finalObject, kv.getKey(), kv.getValue()));
        }

        return root;
    }

    public record JsonMergeOptions(boolean deep, boolean concatArrays) {}

    public Object merge(Object target, Object source, JsonMergeOptions options) {
        var iter = this.entrySet(source);
        if (!jObject.is(target)) {
            throw new IllegalArgumentException("target is not a Json object");
        }
        var targetObj = (JO)target;
        for (var entry : iter) {
            var updateKey = entry.getKey();
            var updateValue = entry.getValue();
            if (jObject.has(targetObj, updateKey)) {
                var targetValue = jObject.get(targetObj, updateKey);
                if (options.concatArrays && jArray.is(targetValue) && jArray.is(updateValue)) {
                    jArray.addAll((JA)targetValue, (JA)updateValue);
                } else if (options.deep && jObject.is(targetValue) && jObject.is(updateValue)) {
                    merge(targetValue, updateValue, options);
                } else {
                    jObject.add(targetObj, updateKey, updateValue);
                }
            } else {
                jObject.add(targetObj, updateKey, updateValue);
            }
        }
        return target;
    }

    public Object merge(Object target, Object source) {
        return merge(target, source, new JsonMergeOptions(false, false));
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
            var obj = jObject.create();
            jObject.add(obj, point, elm);
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
                    res = unwrap(res, true);
                }
                return res;
            }
            return name;
        };
    }
}
