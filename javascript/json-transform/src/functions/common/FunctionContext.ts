import TransformerFunction from "./TransformerFunction";
import {ParameterResolver} from "../../ParameterResolver";
import {JsonTransformerFunction} from "../../JsonTransformerFunction";
import {compareTo, isNullOrUndefined, getAsString, getDocumentContext} from "../../JsonHelpers";

class FunctionContext {
  protected static readonly CONTEXT_KEY= "context";
  protected static readonly DOUBLE_HASH_CURRENT = "##current";
  protected static readonly DOUBLE_HASH_INDEX = "##index";
  protected static readonly DOLLAR = "$";

  protected readonly alias: string;
  protected readonly function: TransformerFunction;
  protected readonly resolver: ParameterResolver;
  protected readonly extractor: JsonTransformerFunction;

  constructor(alias: string, func: TransformerFunction, resolver: ParameterResolver, extractor: JsonTransformerFunction, definition: any = null) {
    this.alias = alias;
    this.function = func;
    this.extractor = extractor;
    if (definition == null) {
      this.resolver = resolver;
    } else {
      this.resolver = this.recalcResolver(definition, resolver, extractor);
    }
  }

  private recalcResolver(definition: any, resolver: ParameterResolver, extractor: JsonTransformerFunction): ParameterResolver {
    if (definition?.[FunctionContext.CONTEXT_KEY]) {
      const contextElement = definition[FunctionContext.CONTEXT_KEY];
      if (typeof contextElement === 'object') {
        const addCtx = Object.entries(contextElement).reduce((a, [key, value]) => {
          a[key] = getDocumentContext(extractor.transform(value, resolver, false));
          return a;
        }, {} as Record<string, any>);
        return {
          get: name => {
            for (const key in addCtx) {
              if (FunctionContext.pathOfVar(key, name)) {
                return addCtx[key].read(FunctionContext.DOLLAR + name.substring(key.length));
              }
            }
            return resolver.get(name);
          }
        }
      }
    }
    return resolver;
  }

  /**
   * Check if the specified path is of the specified variable
   * @param _var variable name
   * @param path  path to check
   * @return true if the path is of the variable
   */
  public static pathOfVar(_var: string, path: string) {
    return path === _var || path.startsWith(_var + ".") || path.startsWith(_var + "[");
  }

  public getAlias() {
    return this.alias;
  }

  public getResolver() {
    return this.resolver;
  }

  protected has(name: string): boolean {
    return false
  };

  protected get(name: string | null, transform: boolean = true): any {
    return null;
  }

  public isNull(value: any) {
    return isNullOrUndefined(value);
  }

  public isJsonNumber(value: any) {
    return typeof value === 'number';
  }

  public isJsonBoolean(value: any) {
    return typeof value === 'boolean';
  }

  public compareTo(a: any, b: any) {
    return compareTo(a, b);
  }

  public getJsonElement(name: string | null, transform: boolean = true) {
    const value = this.get(name, transform);
    // TODO: add stream support
    return value;
  }

  public getBoolean(name: string | null, transform: boolean = true) {
    const value = this.get(name, transform);
    if (value == null) {
      return null;
    }
    if (typeof value === 'boolean') {
      return value;
    }
    const str = getAsString(value);
    if (str == null) return null;
    return str.trim().toLowerCase() === "true";
  }

  public getString(name: string | null, transform: boolean = true) : string | null {
    const value = this.get(name, transform);
    if (value == null) {
      return null;
    }
    // TODO: add stream support
    return getAsString(value);
  }

  public getEnum(name: string | null, transform: boolean = true) {
    const value = this.getString(name, transform);
    if (value == null) {
      return null;
    }
    return value.trim().toUpperCase();
  }

  private getNumber(name: string | null, transform: boolean = true) {
    const value = this.get(name, transform);
    if (value == null) {
      return null;
    }
    if (typeof value === 'number') {
      return Math.floor(value);
    }
    let str = getAsString(value);
    if (str == null) return null;
    str = str.trim();
    if (str === "") return null;
    return parseInt(value);
  }

  public getInteger(name: string | null, transform: boolean = true) {
    return this.getNumber(name, transform)
  }

  public getLong(name: string | null, transform: boolean = true) {
    return this.getNumber(name, transform);
  }

  public getBigDecimal(name: string | null, transform: boolean = true) {
    return this.getNumber(name, transform);
  }

  public getJsonArray(name: string | null, transform: boolean = true) {
    const value = this.get(name, transform);
    // TODO: add stream support
    return Array.isArray(value) ? value : null;
  }

  public getJsonElementStreamer(name: string | null) {
    // TODO: add stream support
  }

  public transform(definition: any, allowReturningStreams: boolean = false) {
    return this.extractor.transform(definition, this.resolver, allowReturningStreams);
  }

  public transformItem(definition: any, current: any, index?: number, additionalName?: string, additional?: any) {
    const currentContext = getDocumentContext(current);
    let itemResolver: ParameterResolver;
    if (typeof index !== 'number') {
      itemResolver = {
        get: name =>
          FunctionContext.pathOfVar(FunctionContext.DOUBLE_HASH_CURRENT, name)
            ? currentContext.read(FunctionContext.DOLLAR + name.substring(9))
            : this.resolver.get(name)
      };
    } else if (!additionalName) {
      itemResolver = {
        get: name =>
          name === FunctionContext.DOUBLE_HASH_INDEX
            ? index
            : FunctionContext.pathOfVar(FunctionContext.DOUBLE_HASH_CURRENT, name)
              ? currentContext.read(FunctionContext.DOLLAR + name.substring(9))
              : this.resolver.get(name)
      }
    } else {
      const additionalContext = getDocumentContext(additional);
      itemResolver = {
        get: name =>
          name === FunctionContext.DOUBLE_HASH_INDEX
            ? index
            : FunctionContext.pathOfVar(FunctionContext.DOUBLE_HASH_CURRENT, name)
                ? currentContext.read(FunctionContext.DOLLAR + name.substring(9))
                : FunctionContext.pathOfVar(additionalName, name)
                  ? additionalContext.read(FunctionContext.DOLLAR + name.substring(additionalName.length))
                  : this.resolver.get(name)
      }
    }
    return this.extractor.transform(definition, itemResolver, false);
  }
}

export default FunctionContext;