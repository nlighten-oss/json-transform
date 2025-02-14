package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
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

    public JsonTransformer(
            final Object definition,
            final JsonAdapter<?, ?, ?> adapter,
            final TransformerFunctionsAdapter functionsAdapter) {
        this.adapter = adapter != null ? adapter : JsonTransformerConfiguration.get().getAdapter();
        this.transformerFunctions = functionsAdapter != null ? functionsAdapter : DEFAULT_TRANSFORMER_FUNCTIONS;
        this.definition = this.adapter.wrap(definition);
        this.JSON_TRANSFORMER = this::fromJsonElement;
    }

    public JsonTransformer(
            final Object definition,
            final JsonAdapter<?, ?, ?> adapter) {
        this(definition, adapter, null);
    }

    public JsonTransformer(
            final Object definition,
            final TransformerFunctionsAdapter functionsAdapter) {
        this(definition, null, functionsAdapter);
    }

    public JsonTransformer(final Object definition) {
        this(definition, DEFAULT_TRANSFORMER_FUNCTIONS);
    }

    public CompletionStage<Object> transform(Object payload, Map<String, Object> additionalContext, boolean allowReturningStreams, boolean unwrap) {
        var result = transform(payload, additionalContext, allowReturningStreams);
        return unwrap ? result.thenApply(adapter::unwrap) : result;
    }

    public CompletionStage<Object> transform(Object payload, Map<String, Object> additionalContext, boolean allowReturningStreams) {
        if (definition == null) {
            return CompletableFuture.completedStage(adapter.jsonNull());
        }
        var resolver = adapter.createPayloadResolver(payload, additionalContext, false);
        return fromJsonElement("$", definition, resolver, allowReturningStreams);
    }

    private CompletionStage<Object> extractMatch(CompletionStage<TransformerFunctions.FunctionMatchResult<Object>> match, boolean allowReturningStreams) {
        return match.thenCompose(x -> {
            var r = x.result();
            if (r instanceof AsyncJsonElementStreamer streamer) {
                return allowReturningStreams
                        ? CompletableFuture.completedStage(streamer)
                        : streamer.toJsonArray();
            }
            if (r instanceof JsonElementStreamer streamer) {
                return CompletableFuture.completedStage(allowReturningStreams
                        ? streamer
                        : streamer.toJsonArray());
            }
            return CompletableFuture.completedStage(adapter.wrap(r));
        });
    }

    protected CompletionStage<Object> fromJsonPrimitive(String path, Object definition, co.nlighten.jsontransform.ParameterResolver resolver, boolean allowReturningStreams) {
        if (!adapter.isJsonString(definition))
            return CompletableFuture.completedStage(definition);
        try {
            var val = adapter.getAsString(definition);
            // test for inline function (e.g. $$function:...)
            var match = transformerFunctions.matchInline(adapter, path, val, resolver, JSON_TRANSFORMER);
            if (match != null) {
                return extractMatch(match, allowReturningStreams);
            }
            // jsonpath / context
            var value = resolver.get(val);
            return CompletableFuture.completedStage(adapter.wrap(value));
        } catch (Exception ee) {
            //logger.trace("Failed getting value for " + definition, ee);
            return null;
        }
    }


    protected CompletionStage<Object> fromJsonObject(String path, Object definition, co.nlighten.jsontransform.ParameterResolver resolver, boolean allowReturningStreams) {
        var match = transformerFunctions.matchObject(adapter, path, definition, resolver, JSON_TRANSFORMER);
        if (match != null) {
            return extractMatch(match, allowReturningStreams);
        }

        var result = adapter.createObject();
        if (adapter.has(definition, OBJ_DESTRUCT_KEY)) {
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
                // don't define key if #null was used
                // might already exist, so try removing it
                adapter.remove(result, localKey);
                continue;
            }
            var value = fromJsonElement(
                    path + JsonTransformerUtils.toObjectFieldPath(adapter, localKey),
                    localValue, resolver, false);
            if (!adapter.isNull(value) || adapter.has(result, localKey) /* we allow overriding with null*/) {
                adapter.add(result, localKey, value);
            }
        }

        return CompletableFuture.completedStage(result);
    }

    protected CompletionStage<Object> fromJsonElement(String path, Object definition, ParameterResolver resolver, boolean allowReturningStreams) {
        if (adapter.isNull(definition))
            return CompletableFuture.completedStage(adapter.jsonNull());
        if (adapter.isJsonArray(definition)) {
            /*
             if (Array.isArray(definition)) {
               return Promise.all(definition.map((d: any, i) => this.fromJsonElement(`${path}[${i}]`, d, resolver, false)));
             }
             */
            var size = adapter.size(definition);
            var result = adapter.createArray(size);
            var index = new AtomicInteger(0);
            return CompletableFuture.allOf(
                adapter.stream(definition, false)
                    .map(d -> {
                        var i = index.getAndIncrement();
                        return fromJsonElement(path + "[" + i + "]", d, resolver, false)
                                .thenApply(item -> {
                                    adapter.set(result, i, item);
                                    return null;
                                }).toCompletableFuture();
                    }).toArray(CompletableFuture[]::new)
            ).thenApply(v -> result);
        }
        if (adapter.isJsonObject(definition)) {
            return fromJsonObject(path, definition, resolver, allowReturningStreams);
        }
        return fromJsonPrimitive(path, definition, resolver, allowReturningStreams);
    }

    private record ElementWithTargetIndex(CompletionStage<Object> element, int targetIndex) {}

    public Object getDefinition() {
        return definition;
    }
}
