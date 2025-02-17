package co.nlighten.jsontransform;

import java.util.Map;

/**
 * The purpose of this interface is to provide a way to resolve parameters in a function.
 */
public interface ParameterResolver {

    static ParameterResolver fromMap(Map<String, ?> map) {
        return map::get;
    }

    /**
     * Get a parameter by name.
     * @param name the name of the parameter
     * @return the parameter value
     */
    Object get(String name);
}
