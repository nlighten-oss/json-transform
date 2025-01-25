package co.nlighten.jsontransform;

@FunctionalInterface
public interface JsonTransformerFunction<JE> {
    /**
     * @return JsonElement | JsonElementStreamer
     */
    Object transform(String path, JE definition, ParameterResolver resolver, boolean allowReturningStreams);
}
