package co.nlighten.jsontransform;

import co.nlighten.jsontransform.ParameterResolver;

@FunctionalInterface
public interface JsonTransformerFunction<JE> {
    /**
     * @return JsonElement | JsonElementStreamer
     */
    Object transform(JE definition, ParameterResolver resolver, boolean allowReturningStreams);
}
