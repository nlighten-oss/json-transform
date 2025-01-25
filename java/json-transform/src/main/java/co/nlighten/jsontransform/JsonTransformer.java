package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import com.google.gson.JsonNull;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A transformer is used to transform data from one layout to another
 */
public abstract class JsonTransformer<JE, JA extends Iterable<JE>, JO extends JE> implements Transformer {

    static final String OBJ_DESTRUCT_KEY = "*";
    static final String NULL_VALUE = "#null";

    private final JsonAdapter<JE, JA, JO> adapter;
    protected final JE definition;

    private final JsonTransformerFunction<JE> JSON_TRANSFORMER;
    private final TransformerFunctionsAdapter<JE, JA, JO> transformerFunctions;

    public JsonTransformer(
            final JsonAdapter<JE, JA, JO> adapter,
            final JE definition,
            final TransformerFunctionsAdapter<JE, JA, JO> functionsAdapter) {
        this.transformerFunctions = functionsAdapter;
        this.adapter = adapter;
        this.definition = definition;
        this.JSON_TRANSFORMER = this::fromJsonElement;
    }

    public Object transform(Object payload, Map<String, Object> additionalContext, boolean allowReturningStreams) {
        if (definition == null) {
            return JsonNull.INSTANCE;
        }
        var resolver = adapter.createPayloadResolver(payload, additionalContext, false);
        return fromJsonElement("$", definition, resolver, allowReturningStreams);
    }

    protected Object fromJsonPrimitive(String path, JE definition, co.nlighten.jsontransform.ParameterResolver resolver, boolean allowReturningStreams) {
        if (!adapter.isJsonString(definition))
            return definition;
        try {
            var val = adapter.getAsString(definition);
            // test for inline function (e.g. $$function:...)
            var match = transformerFunctions.matchInline(path, val, resolver, JSON_TRANSFORMER);
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
            return null;
        }
    }


    protected Object fromJsonObject(String path, JO definition, co.nlighten.jsontransform.ParameterResolver resolver, boolean allowReturningStreams) {
        var match = transformerFunctions.matchObject(path, definition, resolver, JSON_TRANSFORMER);
        if (match != null) {
            var res = match.result();
            return res instanceof JsonElementStreamer s
                    ? (allowReturningStreams ? s : s.toJsonArray())
                    : adapter.wrap(res);
        }

        var result = adapter.jObject.create();
        if (adapter.jObject.has(definition, OBJ_DESTRUCT_KEY)) {
            var val = adapter.jObject.get(definition, OBJ_DESTRUCT_KEY);
            var res = (JE) fromJsonElement(path + "[\"*\"]", val, resolver, false);
            if (res != null) {
                var isArray = adapter.jArray.is(val);
                if (isArray && adapter.jArray.is(res)) {
                    for (JE x: (JA)res) {
                        if (adapter.jObject.is(x)) {
                            var xo = (JO)x;
                            for (Map.Entry<String, JE> e : adapter.jObject.entrySet(xo)) {
                                adapter.jObject.add(result, e.getKey(), e.getValue());
                            }
                        }
                    }
                } else if (adapter.jObject.is(res)) {
                    // override the base object with resolved one
                    result = (JO)res;
                } else {
                    adapter.jObject.add(result, OBJ_DESTRUCT_KEY, res);
                }
            }
        }

        for (Map.Entry<String, JE> kv : adapter.jObject.entrySet(definition)) {
            if (kv.getKey().equals(OBJ_DESTRUCT_KEY)) continue;
            var localKey = kv.getKey();
            var localValue = kv.getValue();
            if (adapter.isJsonString(localValue) && adapter.getAsString(localValue).equals(NULL_VALUE)) {
                // don't define key if #null was used
                // might already exist, so try removing it
                adapter.jObject.remove(result, localKey);
                continue;
            }
            var value = (JE) fromJsonElement(
                    path + JsonTransformerUtils.toObjectFieldPath(adapter, localKey),
                    localValue, resolver, false);
            if (!adapter.isNull(value) || adapter.jObject.has(result, localKey) /* we allow overriding with null*/) {
                adapter.jObject.add(result, localKey, value);
            }
        }

        return result;
    }

    protected Object fromJsonElement(String path, JE definition, ParameterResolver resolver, boolean allowReturningStreams) {
        if (adapter.isNull(definition))
            return adapter.jsonNull();
        if (adapter.jArray.is(definition)) {
            var result = adapter.jArray.create();
            var index = new AtomicInteger(0);
            adapter.jArray.stream((JA)definition)
                    .map(d -> (JE)fromJsonElement(path + "[" + index.getAndIncrement() + "]", d, resolver, false))
                    .forEachOrdered(item -> adapter.jArray.add(result, item));
            return result;
        }
        if (adapter.jObject.is(definition)) {
            return fromJsonObject(path, (JO)definition, resolver, allowReturningStreams);
        }
        return fromJsonPrimitive(path, definition, resolver, allowReturningStreams);
    }

    public JE getDefinition() {
        return definition;
    }
}
