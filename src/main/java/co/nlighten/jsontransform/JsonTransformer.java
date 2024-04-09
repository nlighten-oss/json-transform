package co.nlighten.jsontransform;

import co.nlighten.jsontransform.*;
import co.nlighten.jsontransform.ParameterResolver;
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

        var result = adapter.OBJECT.create();
        if (adapter.OBJECT.has(definition, "*")) {
            var val = adapter.OBJECT.get(definition, "*");
            var res = (JE) fromJsonElement(val, resolver, false);
            if (res != null) {
                var isArray = adapter.ARRAY.is(val);
                if (isArray && adapter.ARRAY.is(res)) {
                    for (JE x: (JA)res) {
                        if (adapter.OBJECT.is(x)) {
                            var xo = (JO)x;
                            for (Map.Entry<String, JE> e : adapter.OBJECT.entrySet(xo)) {
                                adapter.OBJECT.add(result, e.getKey(), e.getValue());
                            }
                        }
                    }
                } else if (adapter.OBJECT.is(res)) {
                    // override the base object with resolved one
                    result = (JO)res;
                } else {
                    adapter.OBJECT.add(result, "*", res);
                }
            }
        }

        for (Map.Entry<String, JE> kv : adapter.OBJECT.entrySet(definition)) {
            if (kv.getKey().equals("*")) continue;
            var localKey = kv.getKey();
            var value = (JE) fromJsonElement(kv.getValue(), resolver, false);
            if (!adapter.isNull(value) || adapter.OBJECT.has(result, localKey) /* we allow overriding with null*/) {
                adapter.OBJECT.add(result, localKey, value);
            }
        }

        return result;
    }

    protected Object fromJsonElement(JE definition, ParameterResolver resolver, boolean allowReturningStreams) {
        if (adapter.isNull(definition))
            return adapter.jsonNull();
        if (adapter.ARRAY.is(definition)) {
            var result = adapter.ARRAY.create();
            adapter.ARRAY.stream((JA)definition)
                    .map(d -> (JE)fromJsonElement(d, resolver, false))
                    .forEachOrdered(item -> adapter.ARRAY.add(result, item));
            return result;
        }
        if (adapter.OBJECT.is(definition)) {
            return fromJsonObject((JO)definition, resolver, allowReturningStreams);
        }
        return fromJsonPrimitive(definition, resolver, allowReturningStreams);
    }

    public JE getDefinition() {
        return definition;
    }
}
