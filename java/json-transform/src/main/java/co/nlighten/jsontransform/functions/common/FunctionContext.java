package co.nlighten.jsontransform.functions.common;

import co.nlighten.jsontransform.ParameterResolver;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.adapters.JsonArrayAdapter;
import co.nlighten.jsontransform.adapters.JsonObjectAdapter;
import co.nlighten.jsontransform.JsonElementStreamer;
import co.nlighten.jsontransform.JsonTransformerFunction;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class FunctionContext<JE, JA extends Iterable<JE>, JO extends JE> {

    protected final String CONTEXT_KEY = "context";
    protected final String DOUBLE_HASH_CURRENT = "##current";
    protected final String DOUBLE_HASH_INDEX = "##index";
    protected final String DOLLAR = "$";

    /**
     * The function's name as it appeared in the transformer (e.g. "$$filter")
     */
    protected final String alias;
    protected final co.nlighten.jsontransform.functions.common.TransformerFunction<JE, JA, JO> function;
    protected final ParameterResolver resolver;
    protected final JsonTransformerFunction<JE> extractor;

    public final JsonArrayAdapter<JE, JA, JO> jArray;
    public final JsonObjectAdapter<JE, JA, JO> jObject;
    protected final JsonAdapter<JE, JA, JO> adapter;

    public FunctionContext(JsonAdapter<JE, JA, JO> jsonAdapter,
                           String alias,
                           co.nlighten.jsontransform.functions.common.TransformerFunction<JE, JA, JO> function,
                           ParameterResolver resolver, JsonTransformerFunction<JE> extractor,
                           JO definition) {
        this.jArray = jsonAdapter.jArray;
        this.jObject = jsonAdapter.jObject;
        this.adapter = jsonAdapter;
        this.alias = alias;
        this.function = function;
        this.extractor = extractor;
        if (definition == null) {
            this.resolver = resolver;
        } else {
            this.resolver = recalcResolver(definition, resolver, extractor);
        }
    }

    public FunctionContext(JsonAdapter<JE,JA,JO> jsonAdapter, String alias, TransformerFunction<JE,JA,JO> function, ParameterResolver resolver, JsonTransformerFunction<JE> extractor) {
        this(jsonAdapter, alias, function, resolver, extractor, null);
    }

    private ParameterResolver recalcResolver(JO definition, ParameterResolver resolver, JsonTransformerFunction<JE> extractor) {
        if (adapter.jObject.has(definition, CONTEXT_KEY)) {
            var contextElement = adapter.jObject.get(definition, CONTEXT_KEY);
            if (adapter.jObject.is(contextElement)) {
                var ctx = adapter.jObject.convert(contextElement);
                var addCtx = adapter.jObject.entrySet(ctx).stream().collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                kv -> adapter.getDocumentContext(extractor.transform(kv.getValue(), resolver, false))
                        )
                );
                return name -> {
                    for (var key : addCtx.keySet()) {
                        if (pathOfVar(key, name)) {
                            return addCtx.get(key).read(DOLLAR + name.substring(key.length()));
                        }
                    }
                    return resolver.get(name);
                };
            }
        }
        return resolver;
    }

    /**
     * Check if the specified path is of the specified variable
     * @param var variable name
     * @param path  path to check
     * @return true if the path is of the variable
     */
    public static boolean pathOfVar(String var, String path){
        return Objects.equals(path, var) || path.startsWith(var + ".") || path.startsWith(var + "[");
    }

    public String getAlias() {
        return alias;
    }

    public ParameterResolver getResolver() {
        return resolver;
    }

    public abstract boolean has(String name);

    public abstract Object get(String name, boolean transform);

    public Object get(String name) {
        return get(name, true);
    }


    public boolean isNull(JE value) {
        return adapter.isNull(value);
    }
    public JE wrap(Object value) {
        return adapter.wrap(value);
    }
    public Object unwrap(JE value) {
        return adapter.unwrap(value, false);
    }
    public Object unwrap(JE value, boolean reduceBigDecimals) {
        return adapter.unwrap(value, reduceBigDecimals);
    }
    public JE parse(String jsonString) {
        return adapter.parse(jsonString);
    }

    public String getAsString(Object value) {
        return adapter.getAsString(value);
    }

    public Object getUnwrapped(String name, boolean reduceBigDecimals) {
        var value = get(name, true);
        if (value instanceof JsonElementStreamer streamer) {
            value = streamer.toJsonArray();
        }
        return adapter.unwrap((JE)value, reduceBigDecimals);
    }

    public Object getUnwrapped(String name) {
        return getUnwrapped(name, false);
    }

    public Integer compareTo(JE a, JE b) {
        return adapter.compareTo(a, b);
    }

    public JE getJsonElement(String name, boolean transform) {
        var value = get(name, transform);
        if (value instanceof JsonElementStreamer streamer) {
            value = streamer.toJsonArray();
        }
        return adapter.wrap(value);
    }

    public JE getJsonElement(String name) {
        return getJsonElement(name, true);
    }

    public Boolean getBoolean(String name, boolean transform) {
        var value = get(name, transform);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean b) {
            return b;
        }
        var str = getAsString(value);
        if (str == null) return null;
        return Boolean.parseBoolean(str.trim());
    }

    public Boolean getBoolean(String name) {
        return getBoolean(name, true);
    }

    public String getString(String name, boolean transform) {
        var value = get(name, transform);
        if (value == null) {
            return null;
        }
        if (value instanceof JsonElementStreamer streamer) {
            value = streamer.toJsonArray();
        }
        return getAsString(value);
    }

    public String getString(String name) {
        return getString(name, true);
    }

    public String getEnum(String name, boolean transform) {
        var value = getString(name, transform);
        if (value == null) return null;
        return value.trim().toUpperCase();
    }

    public String getEnum(String name) {
        return getEnum(name, true);
    }

    public Integer getInteger(String name, boolean transform) {
        var value = get(name, transform);
        if (value == null) {
            return null;
        }
        if (value instanceof Number n) {
            return n.intValue();
        }
        if (adapter.isJsonNumber(value)) {
            return adapter.getNumber((JE)value).intValue();
        }
        var str = getAsString(value);
        if (str == null) return null;
        str = str.trim();
        if (str.isEmpty()) return null;
        return new BigDecimal(str).intValue();
    }

    public Integer getInteger(String name){
        return getInteger(name, true);
    }

    public Long getLong(String name, boolean transform) {
        var value = get(name, transform);
        if (value == null) {
            return null;
        }
        if (value instanceof Number n) {
            return n.longValue();
        }
        if (adapter.isJsonNumber(value)) {
            return adapter.getNumber((JE)value).longValue();
        }
        var str = getAsString(value);
        if (str == null) return null;
        str = str.trim();
        if (str.isEmpty()) return null;
        return new BigDecimal(str).longValue();
    }

    public Long getLong(String name){
        return getLong(name, true);
    }

    public BigDecimal getBigDecimal(String name, boolean transform) {
        var value = get(name, transform);
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal b) {
            return b;
        }
        if (adapter.isJsonNumber(value)) {
            return adapter.getNumberAsBigDecimal((JE)value);
        }
        var str = getAsString(value);
        if (str == null) return null;
        str = str.trim();
        if (str.isEmpty()) return null;
        return new BigDecimal(str);
    }

    public BigDecimal getBigDecimal(String name){
        return getBigDecimal(name, true);
    }

    public JA getJsonArray(String name, boolean transform) {
        var value = get(name, transform);
        if (value instanceof JsonElementStreamer jes) {
            return (JA)jes.toJsonArray();
        }
        var el = adapter.wrap(value);
        if (!adapter.jArray.is(el)) return null;
        return (JA)el;
    }

    public JA getJsonArray(String name) {
        return getJsonArray(name, true);
    }


    /**
     * Use this method instead of getJsonArray if you plan on iterating over the array
     * The pros of using this method are
     * - That it will not transform all the array to a single (possibly huge) array in memory
     * - It lazy transforms the array elements, so if there is short-circuiting, some transformations might be prevented
     * @return JsonElementStreamer
     */
    public JsonElementStreamer<JE, JA, JO> getJsonElementStreamer(String name) {
        var transformed = false;
        var value = get(name, false);
        if (value instanceof JsonElementStreamer jes) {
            return jes;
        }
        // in case val is already an array we don't transform it to prevent evaluation of its result values
        // so if is not an array, we must transform it and check after-wards (not lazy anymore)
        if (!adapter.jArray.is(value)) {
            value = extractor.transform(wrap(value), resolver, true);
            if (value instanceof JsonElementStreamer jes) {
                return jes;
            }
            transformed = true;
        }
        // check if initially or after transformation we got an array
        if (adapter.jArray.is(value)) {
            return JsonElementStreamer.fromJsonArray(this, (JA)value, transformed);
        }
        return null;
    }

    public JE transform(JE definition){
        return (JE) extractor.transform(definition, resolver, false);
    }

    public Object transform(JE definition, boolean allowReturningStreams){
        return extractor.transform(definition, resolver, allowReturningStreams);
    }

    public JE transformItem(JE definition, JE current) {
        var currentContext = adapter.getDocumentContext(current);
        ParameterResolver itemResolver = name ->
                pathOfVar(DOUBLE_HASH_CURRENT, name)
                ? currentContext.read(DOLLAR + name.substring(9))
                : resolver.get(name);
        return (JE) extractor.transform(definition, itemResolver, false);
    }

    public JE transformItem(JE definition, JE current, Integer index) {
        var currentContext = adapter.getDocumentContext(current);
        ParameterResolver itemResolver = name ->
                DOUBLE_HASH_INDEX.equals(name)
                ? wrap(index)
                : pathOfVar(DOUBLE_HASH_CURRENT, name)
                  ? currentContext.read(DOLLAR + name.substring(9))
                  : resolver.get(name);
        return (JE) extractor.transform(definition, itemResolver, false);
    }

    public JE transformItem(JE definition, JE current, Integer index, String additionalName, JE additional) {
        var currentContext = adapter.getDocumentContext(current);
        var additionalContext = adapter.getDocumentContext(additional);
        ParameterResolver itemResolver = name ->
                DOUBLE_HASH_INDEX.equals(name)
                ? wrap(index)
                : pathOfVar(DOUBLE_HASH_CURRENT, name)
                  ? currentContext.read(DOLLAR + name.substring(9))
                  : pathOfVar(additionalName, name)
                    ? additionalContext.read(DOLLAR + name.substring(additionalName.length()))
                    : resolver.get(name);
        return (JE) extractor.transform(definition, itemResolver, false);
    }

    public JE transformItem(JE definition, JE current, Integer index, Map<String, JE> additionalContexts) {
        var currentContext = adapter.getDocumentContext(current);
        var addCtx = additionalContexts.keySet().stream().collect(
                Collectors.toMap(key -> key, key -> adapter.getDocumentContext(additionalContexts.get(key))));
        ParameterResolver itemResolver = name -> {
            if (DOUBLE_HASH_INDEX.equals(name)) return wrap(index);
            if (pathOfVar(DOUBLE_HASH_CURRENT, name)) return currentContext.read("$" + name.substring(9));
            for (var key : additionalContexts.keySet()) {
                if (pathOfVar(key, name)) {
                    return addCtx.get(key).read(DOLLAR + name.substring(key.length()));
                }
            }
            return resolver.get(name);
        };
        return (JE) extractor.transform(definition, itemResolver, false);
    }
}
