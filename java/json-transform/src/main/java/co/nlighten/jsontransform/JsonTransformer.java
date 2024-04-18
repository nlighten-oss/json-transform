package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import com.google.gson.JsonNull;

import java.util.Map;

/**
 * A transformer is used to transform data from one layout to another
 */
public abstract class JsonTransformer<JE, JA extends Iterable<JE>, JO extends JE> implements Transformer {

    private final JsonAdapter<JE, JA, JO> adapter;
    protected final JE definition;

    private final JsonTransformerFunction<JE> JSON_TRANSFORMER;
    private final TransformerFunctions<JE, JA, JO> transformerFunctions;

    public JsonTransformer(
            final TransformerFunctions<JE, JA, JO> transformerFunctions,
            final JsonAdapter<JE, JA, JO> adapter,
            final JE definition) {
        this.transformerFunctions = transformerFunctions;
        this.adapter = adapter;
        this.definition = definition;
        this.JSON_TRANSFORMER = this::fromJsonElement;
    }

    public Object transform(Object payload, Map<String, Object> additionalContext) {
        if (definition == null) {
            return JsonNull.INSTANCE;
        }
        var resolver = adapter.createPayloadResolver(payload, additionalContext, false);
        return fromJsonElement(definition, resolver, false);
    }

    protected Object fromJsonPrimitive(JE definition, co.nlighten.jsontransform.ParameterResolver resolver, boolean allowReturningStreams) {
        if (!adapter.isJsonString(definition))
            return definition;
        try {
            var val = adapter.getAsString(definition);
            // test for inline function (e.g. $$function:...)
            var match = transformerFunctions.matchInline(val, resolver, JSON_TRANSFORMER);
            if (match != null) {
                var matchResult = match.getResult();
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


    protected Object fromJsonObject(JO definition, co.nlighten.jsontransform.ParameterResolver resolver, boolean allowReturningStreams) {
        var match = transformerFunctions.matchObject(definition, resolver, JSON_TRANSFORMER);
        if (match != null) {
            var res = match.getResult();
            return res instanceof JsonElementStreamer s
                    ? (allowReturningStreams ? s : s.toJsonArray())
                    : adapter.wrap(res);
        }

        var result = adapter.jObject.create();
        if (adapter.jObject.has(definition, "*")) {
            var val = adapter.jObject.get(definition, "*");
            var res = (JE) fromJsonElement(val, resolver, false);
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
                    adapter.jObject.add(result, "*", res);
                }
            }
        }

        for (Map.Entry<String, JE> kv : adapter.jObject.entrySet(definition)) {
            if (kv.getKey().equals("*")) continue;
            var localKey = kv.getKey();
            var value = (JE) fromJsonElement(kv.getValue(), resolver, false);
            if (!adapter.isNull(value) || adapter.jObject.has(result, localKey) /* we allow overriding with null*/) {
                adapter.jObject.add(result, localKey, value);
            }
        }

        return result;
    }

    protected Object fromJsonElement(JE definition, ParameterResolver resolver, boolean allowReturningStreams) {
        if (adapter.isNull(definition))
            return adapter.jsonNull();
        if (adapter.jArray.is(definition)) {
            var result = adapter.jArray.create();
            adapter.jArray.stream((JA)definition)
                    .map(d -> (JE)fromJsonElement(d, resolver, false))
                    .forEachOrdered(item -> adapter.jArray.add(result, item));
            return result;
        }
        if (adapter.jObject.is(definition)) {
            return fromJsonObject((JO)definition, resolver, allowReturningStreams);
        }
        return fromJsonPrimitive(definition, resolver, allowReturningStreams);
    }

    public JE getDefinition() {
        return definition;
    }
}
