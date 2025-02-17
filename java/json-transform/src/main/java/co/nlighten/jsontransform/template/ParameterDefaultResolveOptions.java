package co.nlighten.jsontransform.template;

/**
 * Resolving types for parameter default values
 */
public enum ParameterDefaultResolveOptions {
    /**
     * Each instance of a parameter is resolved to its explicit default
     */
    UNIQUE,
    /**
     * The first default found for the parameter is used by all
     */
    FIRST_VALUE,
    /**
     * The last default found is used by all
     */
    LAST_VALUE
}