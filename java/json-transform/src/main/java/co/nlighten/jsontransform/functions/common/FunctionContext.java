package co.nlighten.jsontransform.functions.common;

import co.nlighten.jsontransform.JsonTransformerUtils;
import co.nlighten.jsontransform.ParameterResolver;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.JsonElementStreamer;
import co.nlighten.jsontransform.JsonTransformerFunction;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class FunctionContext {

    protected final String CONTEXT_KEY = "context";
    protected final String DOUBLE_HASH_CURRENT = "##current";
    protected final String DOUBLE_HASH_INDEX = "##index";
    protected final String DOLLAR = "$";

    protected final String path;
    /**
     * The function's name as it appeared in the transformer (e.g. "$$filter")
     */
    protected final String alias;
    protected final TransformerFunction function;
    protected final ParameterResolver resolver;
    protected final JsonTransformerFunction extractor;

    protected final JsonAdapter<?, ?, ?> adapter;

    public FunctionContext(String path,
                           JsonAdapter<?, ?, ?> jsonAdapter,
                           String alias,
                           TransformerFunction function,
                           ParameterResolver resolver,
                           JsonTransformerFunction extractor,
                           Object definition) {
        this.adapter = jsonAdapter;
        this.path = path;
        this.alias = alias;
        this.function = function;
        this.extractor = extractor;
        if (definition == null) {
            this.resolver = resolver;
        } else {
            this.resolver = recalcResolver(definition, resolver, extractor);
        }
    }

    public FunctionContext(String path,
                           JsonAdapter<?, ?, ?> jsonAdapter,
                           String alias,
                           TransformerFunction function,
                           ParameterResolver resolver,
                           JsonTransformerFunction extractor) {
        this(path, jsonAdapter, alias, function, resolver, extractor, null);
    }

    private ParameterResolver recalcResolver(Object definition, ParameterResolver resolver, JsonTransformerFunction extractor) {
        if (adapter.has(definition, CONTEXT_KEY)) {
            var contextElement = adapter.get(definition, CONTEXT_KEY);
            if (adapter.isJsonObject(contextElement)) {
                var addCtx = adapter.entrySet(contextElement).stream().collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                kv -> adapter.getDocumentContext(extractor.transform(path + JsonTransformerUtils.toObjectFieldPath(adapter, kv.getKey()),kv.getValue(), resolver, false))
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

    public String getPath() {
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

    public String getPathFor(String name) {
        return this.path;
    }
    public String getPathFor(int index) {
        return this.path + "[" + index + "]";
    }

    public JsonAdapter<?,?,?> getAdapter() {
        return adapter;
    }

    public String getAsString(Object value) {
        return adapter.getAsString(value);
    }

    public Object getUnwrapped(String name, boolean reduceBigDecimals) {
        var value = get(name, true);
        if (value instanceof JsonElementStreamer streamer) {
            value = streamer.toJsonArray();
        }
        return adapter.unwrap(value, reduceBigDecimals);
    }

    public Object getUnwrapped(String name) {
        return getUnwrapped(name, false);
    }

    public Object getJsonElement(String name, boolean transform) {
        var value = get(name, transform);
        if (value instanceof JsonElementStreamer streamer) {
            value = streamer.toJsonArray();
        }
        return adapter.wrap(value);
    }

    public Object getJsonElement(String name) {
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
            return adapter.getNumber(value).intValue();
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
            return adapter.getNumber(value).longValue();
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
            return adapter.getNumberAsBigDecimal(value);
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

    public Object getJsonArray(String name, boolean transform) {
        var value = get(name, transform);
        if (value instanceof JsonElementStreamer jes) {
            return jes.toJsonArray();
        }
        var el = adapter.wrap(value);
        if (!adapter.isJsonArray(el)) return null;
        return el;
    }

    public Object getJsonArray(String name) {
        return getJsonArray(name, true);
    }


    /**
     * Use this method instead of getJsonArray if you plan on iterating over the array
     * The pros of using this method are
     * - That it will not transform all the array to a single (possibly huge) array in memory
     * - It lazy transforms the array elements, so if there is short-circuiting, some transformations might be prevented
     * @return JsonElementStreamer
     */
    public JsonElementStreamer getJsonElementStreamer(String name) {
        var transformed = false;
        var value = get(name, false);
        if (value instanceof JsonElementStreamer jes) {
            return jes;
        }
        // in case val is already an array we don't transform it to prevent evaluation of its result values
        // so if is not an array, we must transform it and check after-wards (not lazy anymore)
        if (!adapter.isJsonArray(value)) {
            value = extractor.transform(getPathFor(name), adapter.wrap(value), resolver, true);
            if (value instanceof JsonElementStreamer jes) {
                return jes;
            }
            transformed = true;
        }
        // check if initially or after transformation we got an array
        if (adapter.isJsonArray(value)) {
            return JsonElementStreamer.fromJsonArray(this, value, transformed);
        }
        return null;
    }

    // TODO: replace this with something
    public Object transform(Object definition){
        return extractor.transform(path, definition, resolver, false);
    }

    public Object transform(String path, Object definition){
        return extractor.transform(path, definition, resolver, false);
    }

    public Object transform(String path, Object definition, boolean allowReturningStreams){
        return extractor.transform(path, definition, resolver, allowReturningStreams);
    }

    public Object transformItem(Object definition, Object current) {
        var currentContext = adapter.getDocumentContext(current);
        ParameterResolver itemResolver = name ->
                pathOfVar(DOUBLE_HASH_CURRENT, name)
                ? currentContext.read(DOLLAR + name.substring(9))
                : resolver.get(name);
        return extractor.transform("$", definition, itemResolver, false);
    }

    public Object transformItem(Object definition, Object current, Integer index) {
        var currentContext = adapter.getDocumentContext(current);
        ParameterResolver itemResolver = name ->
                DOUBLE_HASH_INDEX.equals(name)
                ? adapter.wrap(index)
                : pathOfVar(DOUBLE_HASH_CURRENT, name)
                  ? currentContext.read(DOLLAR + name.substring(9))
                  : resolver.get(name);
        return extractor.transform("$", definition, itemResolver, false);
    }

    public Object transformItem(Object definition, Object current, Integer index, String additionalName, Object additional) {
        var currentContext = adapter.getDocumentContext(current);
        var additionalContext = adapter.getDocumentContext(additional);
        ParameterResolver itemResolver = name ->
                DOUBLE_HASH_INDEX.equals(name)
                ? adapter.wrap(index)
                : pathOfVar(DOUBLE_HASH_CURRENT, name)
                  ? currentContext.read(DOLLAR + name.substring(9))
                  : pathOfVar(additionalName, name)
                    ? additionalContext.read(DOLLAR + name.substring(additionalName.length()))
                    : resolver.get(name);
        return extractor.transform("$", definition, itemResolver, false);
    }

    public Object transformItem(Object definition, Object current, Integer index, Map<String, Object> additionalContexts) {
        var currentContext = adapter.getDocumentContext(current);
        var addCtx = additionalContexts.keySet().stream().collect(
                Collectors.toMap(key -> key, key -> adapter.getDocumentContext(additionalContexts.get(key))));
        ParameterResolver itemResolver = name -> {
            if (DOUBLE_HASH_INDEX.equals(name)) return adapter.wrap(index);
            if (pathOfVar(DOUBLE_HASH_CURRENT, name)) return currentContext.read("$" + name.substring(9));
            for (var key : additionalContexts.keySet()) {
                if (pathOfVar(key, name)) {
                    return addCtx.get(key).read(DOLLAR + name.substring(key.length()));
                }
            }
            return resolver.get(name);
        };
        return extractor.transform("$", definition, itemResolver, false);
    }
}
