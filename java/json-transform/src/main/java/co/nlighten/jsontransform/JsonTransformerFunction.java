package co.nlighten.jsontransform;

import java.util.concurrent.CompletionStage;

@FunctionalInterface
public interface JsonTransformerFunction {
    /**
     * @return JsonElement | JsonElementStreamer
     */
    CompletionStage<Object> transform(String path, Object definition, ParameterResolver resolver, boolean allowReturningStreams);
}
