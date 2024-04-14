package co.nlighten.jsontransform;

@FunctionalInterface
public interface JsonTransformerFunction<JE> {
    /**
     * @return JsonElement | JsonElementStreamer
     */
    Object transform(JE definition, ParameterResolver resolver, boolean allowReturningStreams);
}
