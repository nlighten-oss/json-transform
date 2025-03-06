package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A transformer is used to transform data from one layout to another
 */
public class JsonTransformer implements Transformer {

    static final String OBJ_DESTRUCT_KEY = "*";
    static final String NULL_VALUE = "#null";

    private final JsonAdapter<?, ?, ?> adapter;
    protected final Object definition;

    private static final TransformerFunctionsAdapter DEFAULT_TRANSFORMER_FUNCTIONS = new TransformerFunctions();
    private final JsonTransformerFunction JSON_TRANSFORMER;
    private final TransformerFunctionsAdapter transformerFunctions;

    /**
     * Creates a new JSON transformer from definition
     *
     * @param definition       The transformer definition
     * @param adapter          (optional) A specific JSON implementation adapter (otherwise uses the configured default)
     * @param functionsAdapter (optional) A specific transformer functions adapter (otherwise uses the default)
     */
    public JsonTransformer(
            final Object definition,
            final JsonAdapter<?, ?, ?> adapter,
            final TransformerFunctionsAdapter functionsAdapter) {
        this.adapter = adapter != null ? adapter : JsonTransformerConfiguration.get().getAdapter();
        this.transformerFunctions = functionsAdapter != null ? functionsAdapter : DEFAULT_TRANSFORMER_FUNCTIONS;
        this.definition = this.adapter.wrap(definition);
        this.JSON_TRANSFORMER = this::fromJsonElement;
    }

    /**
     * Creates a new JSON transformer from definition
     *
     * @param definition       The transformer definition
     * @param adapter          (optional) A specific JSON implementation adapter (otherwise uses the configured default)
     */
    public JsonTransformer(
            final Object definition,
            final JsonAdapter<?, ?, ?> adapter) {
        this(definition, adapter, null);
    }

    /**
     * Creates a new JSON transformer from definition
     *
     * @param definition       The transformer definition
     * @param functionsAdapter (optional) A specific transformer functions adapter (otherwise uses the default)
     */
    public JsonTransformer(
            final Object definition,
            final TransformerFunctionsAdapter functionsAdapter) {
        this(definition, null, functionsAdapter);
    }

    /**
     * Creates a new JSON transformer from definition
     *
     * @param definition       The transformer definition
     */
    public JsonTransformer(final Object definition) {
        this(definition, DEFAULT_TRANSFORMER_FUNCTIONS);
    }

    /**
     * Transforms the payload using the transformer definition
     *
     * @param payload               The payload to transform
     * @param additionalContext     (optional) Additional context to use in the transformation
     * @param unwrap                (optional) Unwrap the result to POJO from the used JSON implementation (default is false)
     */
    @Override
    public Object transform(Object payload, Map<String, Object> additionalContext, boolean unwrap) {
        if (definition == null) {
            return unwrap ? null : adapter.jsonNull();
        }
        var resolver = adapter.createPayloadResolver(payload, additionalContext, false);
        var result = fromJsonElement("$", definition, resolver, false);
        return unwrap ? adapter.unwrap(result) : result;
    }


    protected Object fromJsonPrimitive(String path, Object definition, ParameterResolver resolver, boolean allowReturningStreams) {
        if (!adapter.isJsonString(definition))
            return definition;
        try {
            var val = adapter.getAsString(definition);
            // test for inline function (e.g. $$function:...)
            var match = transformerFunctions.matchInline(adapter, path, val, resolver, JSON_TRANSFORMER);
            if (match != null) {
                var matchResult = match.result();
                if (matchResult instanceof JsonElementStreamer streamer) {
                    return allowReturningStreams ? streamer : streamer.toJsonArray();
                }
                return adapter.wrap(matchResult);
            }
            // jsonpath / context
            var value = resolver.get(val);
            return adapter.wrap(value);
        } catch (Exception ee) {
            //logger.trace("Failed getting value for " + definition, ee);
            return adapter.jsonNull();
        }
    }

    protected Object fromJsonObject(String path, Object definition, ParameterResolver resolver, boolean allowReturningStreams) {
        var match = transformerFunctions.matchObject(adapter, path, definition, resolver, JSON_TRANSFORMER);
        if (match != null) {
            var res = match.result();
            return res instanceof JsonElementStreamer s
                    ? (allowReturningStreams ? s : s.toJsonArray())
                    : adapter.wrap(res);
        }

        var result = adapter.createObject();
        var hasObjectDestruction = adapter.has(definition, OBJ_DESTRUCT_KEY);
        if (hasObjectDestruction) {
            var val = adapter.get(definition, OBJ_DESTRUCT_KEY);
            var res = fromJsonElement(path + "[\"*\"]", val, resolver, false);
            if (res != null) {
                var isArray = adapter.isJsonArray(val);
                if (isArray && adapter.isJsonArray(res)) {
                    for (var x: adapter.asIterable(res)) {
                        if (adapter.isJsonObject(x)) {
                            for (Map.Entry<String, ?> e : adapter.entrySet(x)) {
                                adapter.add(result, e.getKey(), e.getValue());
                            }
                        }
                    }
                } else if (adapter.isJsonObject(res)) {
                    // override the base object with resolved one
                    result = res;
                } else {
                    adapter.add(result, OBJ_DESTRUCT_KEY, res);
                }
            }
        }

        for (Map.Entry<String, ?> kv : adapter.entrySet(definition)) {
            if (kv.getKey().equals(OBJ_DESTRUCT_KEY)) continue;
            var localKey = kv.getKey();
            var localValue = kv.getValue();
            if (adapter.isJsonString(localValue) && adapter.getAsString(localValue).equals(NULL_VALUE)) {
                if (hasObjectDestruction) {
                    // don't define key if #null was used
                    // might already exist, so try removing it
                    adapter.remove(result, localKey);
                } else {
                    adapter.add(result, localKey, adapter.jsonNull());
                }
                continue;
            }
            var value = fromJsonElement(
                    path + JsonTransformerUtils.toObjectFieldPath(adapter, localKey),
                    localValue, resolver, false);
            if (!adapter.isNull(value) || adapter.has(result, localKey) /* we allow overriding with null*/) {
                adapter.add(result, localKey, value);
            }
        }

        return result;
    }

    protected Object fromJsonElement(String path, Object definition, ParameterResolver resolver, boolean allowReturningStreams) {
        if (adapter.isNull(definition))
            return adapter.jsonNull();
        if (adapter.isJsonArray(definition)) {
            var result = adapter.createArray();
            var index = new AtomicInteger(0);
            adapter.stream(definition, false)
                    .map(d -> fromJsonElement(path + "[" + index.getAndIncrement() + "]", d, resolver, false))
                    .forEachOrdered(item -> adapter.add(result, item));
            return result;
        }
        if (adapter.isJsonObject(definition)) {
            return fromJsonObject(path, definition, resolver, allowReturningStreams);
        }
        return fromJsonPrimitive(path, definition, resolver, allowReturningStreams);
    }

    /**
     * Gets the transformer definition
     */
    public Object getDefinition() {
        return definition;
    }
}
