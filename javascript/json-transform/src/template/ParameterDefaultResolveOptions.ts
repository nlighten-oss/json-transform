/**
 * Resolving types for parameter default values
 */
enum ParameterDefaultResolveOptions {
  /**
   * Each instance of a parameter is resolved to its explicit default
   */
  UNIQUE = "UNIQUE",
  /**
   * The first default found for the parameter is used by all
   */
  FIRST_VALUE = "FIRST_VALUE",
  /**
   * The last default found is used by all
   */
  LAST_VALUE = "LAST_VALUE",
}

export default ParameterDefaultResolveOptions;
