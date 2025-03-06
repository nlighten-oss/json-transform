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
        return (input, additionalContext, unwrap) -> input;
    }

    /**
     * Transforms the payload using the transformer definition
     *
     * @param payload               The payload to transform
     * @param additionalContext     (optional) Additional context to use in the transformation
     * @param unwrap                (optional) Unwrap the result to POJO from the used implementation (default is false)
     */
    Object transform(Object payload, Map<String, Object> additionalContext, boolean unwrap);

    /**
     * Transforms the payload using the transformer definition
     *
     * @param payload               The payload to transform
     * @param additionalContext     (optional) Additional context to use in the transformation
     */
    default Object transform(Object payload, Map<String, Object> additionalContext) {
        return transform(payload, additionalContext, false);
    }

    /**
     * Transforms the payload using the transformer definition
     *
     * @param payload               The payload to transform
     * @param unwrap                (optional) Unwrap the result to POJO from the used implementation (default is false)
     */
    default Object transform(Object payload, boolean unwrap) {
        return transform(payload, null, unwrap);
    }

    /**
     * Transforms the payload using the transformer definition
     *
     * @param payload               The payload to transform
     */
    default Object transform(Object payload) {
        return transform(payload, null);
    }

    /**
     * transforms based on the transformer definition alone
     *
     * @return transformed data
     */
    default Object transform() {
        return transform(null);
    }
}
