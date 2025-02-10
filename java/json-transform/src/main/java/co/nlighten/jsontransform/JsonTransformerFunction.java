package co.nlighten.jsontransform;

@FunctionalInterface
public interface JsonTransformerFunction {
    /**
     * @return JsonElement | JsonElementStreamer
     */
    Object transform(String path, Object definition, ParameterResolver resolver, boolean allowReturningStreams);
}
