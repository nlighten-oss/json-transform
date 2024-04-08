package co.nlighten.jsontransform.functions;

/**
 * The purpose of this class is to differentiate between null and null result
 * @param <T>
 */
public class FunctionMatchResult<T> {
    final T result;

    /**
     * Wrap a function result.
     * @param result the result of the function
     */
    public FunctionMatchResult(T result) {
        this.result = result;
    }

    /**
     * @return the result of the function
     */
    public T getResult() {
        return result;
    }
}
