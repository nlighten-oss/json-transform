package co.nlighten.jsontransform;

/**
 * The purpose of this interface is to provide a way to resolve parameters in a function.
 */
public interface ParameterResolver {
    /**
     * Get a parameter by name.
     * @param name the name of the parameter
     * @return the parameter value
     */
    Object get(String name);
}
