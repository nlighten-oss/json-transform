import {ArgumentType} from "./ArgumentType";
import {ArgType} from "./ArgType";
import FunctionContext from "./FunctionContext";
import {isNullOrUndefined} from "../../JsonHelpers";
import {FunctionDescription} from "./FunctionDescription";

/**
 * Base class for all transformer functions.
 */
class TransformerFunction {
  protected readonly defaultValues: Record<string, any>;
  protected readonly description: FunctionDescription;

  constructor(description: FunctionDescription) {
    this.description = description;
    this.defaultValues = {};
    const args = description.arguments;
    if (args) {
      for (const arg in args) {
        if (args[arg].aliases?.length) {
          args[arg].aliases?.forEach(alias => {
            args[alias] = args[arg];
          })
        }
        this.defaultValues[arg] = TransformerFunction.getDefaultValue(args[arg]);
      }
    }
  }

  private static getDefaultValue(a: ArgumentType) {
    if (a == null || a.defaultIsNull) return null;
    switch (a.type) {
      case ArgType.Boolean: return a.defaultBoolean;
      case ArgType.String: return a.defaultString;
      case ArgType.Enum: return a.defaultEnum;
      case ArgType.Integer: return a.defaultInteger;
      case ArgType.Long: return a.defaultLong;
      case ArgType.BigDecimal: return a.defaultBigDecimal;
    }
    return null;
  }

  /**
   * Apply the function to the given context.
   * @param context the context
   * @return the result of the function
   */
  public apply(context: FunctionContext): any {}

  /**
   * Get the argument type for the given name.
   * @param name the name of the argument (null will return the primary argument)
   * @return the argument type or null if not found
   */
  public getArgument(name: string) : ArgumentType | null {
    if (isNullOrUndefined(name)) return null;
    return this.description.arguments?.[name] ?? null;
  }

  /**
   * Get the arguments for this function.
   * @return the function arguments
   */
  public getArguments() {
    return this.description.arguments;
  }

  /**
   * Get the input type for this function.
   * @return the input type
   */
  public getInputType() {
    return this.description.inputType;
  }

  /**
   * Get the entire function description.
   * @return the function description
   */
  public getFunctionDescription() {
    return this.description;
  }

  /**
   * Get the default value for the given argument name.
   * @param name the argument name
   * @return the default value or null if not found
   */
  public getDefaultValue(name: string) {
    if (name == null) return null;
    return this.defaultValues[name];
  }
}

export default TransformerFunction;