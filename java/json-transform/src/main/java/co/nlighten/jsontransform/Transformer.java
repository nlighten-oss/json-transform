package co.nlighten.jsontransform;

import java.util.Map;

/**
 * A transformer is used to transform data from one layout to another
 */
public interface Transformer {
    /**
     * A transformer that does not do any transformation
     * @return The transformer input
     */
    static Transformer RAW() {
        return (input, additionalContext, allowReturningStreams) -> input;
    }

    /**
     * transforms based on the transformer specification
     *
     * @return transformed data
     */
    default Object transform() {
        return transform(null, null);
    }

    /**
     * transforms the given data based on the transformer specification
     *
     * @param input the data to transform
     * @return transformed data
     */
    default Object transform(Object input) {
        return transform(input, null);
    }

    /**
     * transforms the given data based on the transformer specification
     * @param input the data to transform
     * @param additionalContext additional context to use during transformation
     * @return transformed data
     */
    default Object transform(Object input, Map<String, Object> additionalContext) {
        return transform(input, additionalContext, false);
    }

    /**
     * transforms the given data based on the transformer specification
     * @param input the data to transform
     * @param additionalContext additional context to use during transformation
     * @param allowReturningStreams allow returning json streams instead of arrays as transformation result
     * @return transformed data
     */
    Object transform(Object input, Map<String, Object> additionalContext, boolean allowReturningStreams);
}
