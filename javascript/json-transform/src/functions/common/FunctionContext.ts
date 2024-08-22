import TransformerFunction from "./TransformerFunction";
import { ParameterResolver } from "../../ParameterResolver";
import { JsonTransformerFunction } from "../../JsonTransformerFunction";
import { compareTo, isNullOrUndefined, getAsString, getDocumentContext, isMap } from "../../JsonHelpers";
import { BigDecimal } from "./FunctionHelpers";
import JsonElementStreamer from "../../JsonElementStreamer";

class FunctionContext {
  protected static readonly CONTEXT_KEY = "context";
  protected static readonly DOUBLE_HASH_CURRENT = "##current";
  protected static readonly DOUBLE_HASH_INDEX = "##index";
  protected static readonly DOLLAR = "$";

  protected readonly alias: string;
  protected readonly function: TransformerFunction;
  protected readonly resolver: ParameterResolver;
  protected readonly extractor: JsonTransformerFunction;

  constructor(
    alias: string,
    func: TransformerFunction,
    resolver: ParameterResolver,
    extractor: JsonTransformerFunction,
    definition: any = null,
  ) {
    this.alias = alias;
    this.function = func;
    this.extractor = extractor;
    if (definition == null) {
      this.resolver = resolver;
    } else {
      this.resolver = this.recalcResolver(definition, resolver, extractor);
    }
  }

  private recalcResolver(
    definition: any,
    resolver: ParameterResolver,
    extractor: JsonTransformerFunction,
  ): ParameterResolver {
    if (definition?.[FunctionContext.CONTEXT_KEY]) {
      const contextElement = definition[FunctionContext.CONTEXT_KEY];
      if (isMap(contextElement)) {
        const addCtx = Object.entries(contextElement).reduce(
          (a, [key, value]) => {
            a[key] = getDocumentContext(extractor.transform(value, resolver, false));
            return a;
          },
          {} as Record<string, any>,
        );
        return {
          get: name => {
            for (const key in addCtx) {
              if (FunctionContext.pathOfVar(key, name)) {
                return addCtx[key].read(FunctionContext.DOLLAR + name.substring(key.length));
              }
            }
            return resolver.get(name);
          },
        };
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

  public has(name: string): boolean {
    return false;
  }

  public async get(name: string | null, transform: boolean = true): Promise<any> {
    return null;
  }

  public isNull(value: any) {
    return isNullOrUndefined(value);
  }

  public async getUnwrapped(name: string | null, reduceBigDecimals?: boolean) {
    const value = await this.get(name, true);
    if (value instanceof JsonElementStreamer) {
      return await value.toJsonArray();
    }
    return value;
  }

  public compareTo(a: any, b: any) {
    return compareTo(a, b);
  }

  public async getJsonElement(name: string | null, transform: boolean = true) {
    const value = await this.get(name, transform);
    if (value instanceof JsonElementStreamer) {
      return await value.toJsonArray();
    }
    return value;
  }

  public async getBoolean(name: string | null, transform: boolean = true) {
    const value = await this.get(name, transform);
    if (value == null) {
      return null;
    }
    if (typeof value === "boolean") {
      return value;
    }
    const str = getAsString(value);
    if (str == null) return null;
    return str.trim().toLowerCase() === "true";
  }

  public async getString(name: string | null, transform: boolean = true): Promise<string | null> {
    const value = await this.get(name, transform);
    if (value == null) {
      return null;
    }
    // TODO: add stream support
    return getAsString(value);
  }

  public async getEnum(name: string | null, transform: boolean = true) {
    const value = await this.getString(name, transform);
    if (value == null) {
      return null;
    }
    return value.trim().toUpperCase();
  }

  public async getInteger(name: string | null, transform: boolean = true) {
    const value = await this.get(name, transform);
    if (value == null) {
      return null;
    }
    if (value instanceof BigDecimal) {
      return Math.floor(value.toNumber());
    }
    if (typeof value === "number") {
      return Math.floor(value);
    }
    if (typeof value === "bigint") {
      return Number(value);
    }
    let str = getAsString(value);
    if (str == null) return null;
    str = str.trim();
    if (str === "") return null;
    return parseInt(value);
  }

  public async getLong(name: string | null, transform: boolean = true) {
    const value = await this.get(name, transform);
    if (value == null) {
      return null;
    }
    if (typeof value === "bigint") {
      return value;
    }
    if (typeof value === "number") {
      return BigInt(value);
    }
    if (value instanceof BigDecimal) {
      return BigInt(value.toFixed(0));
    }
    let str = getAsString(value);
    if (str == null) return null;
    str = str.trim();
    if (str === "") return null;
    return BigInt(value);
  }

  public async getBigDecimal(name: string | null, transform: boolean = true) {
    const value = await this.get(name, transform);
    if (value == null) {
      return null;
    }
    if (value instanceof BigDecimal) {
      return value;
    }
    if (typeof value === "number") {
      return new BigDecimal(value);
    }
    let str = getAsString(value);
    if (str == null) return null;
    str = str.trim();
    if (str === "") return null;
    return new BigDecimal(value);
  }

  public async getJsonArray(name: string | null, transform: boolean = true) {
    const value = await this.get(name, transform);
    if (value instanceof JsonElementStreamer) {
      return await value.toJsonArray();
    }
    return Array.isArray(value) ? value : null;
  }

  /**
   * Use this method instead of getJsonArray if you plan on iterating over the array
   * The pros of using this method are
   * - That it will not transform all the array to a single (possibly huge) array in memory
   * - It lazy transforms the array elements, so if there is short-circuiting, some transformations might be prevented
   * @return JsonElementStreamer
   */
  public async getJsonElementStreamer(name: string | null) {
    let transformed = false;
    let value = await this.get(name, false);
    if (value instanceof JsonElementStreamer) {
      return value;
    }
    // in case val is already an array we don't transform it to prevent evaluation of its result values
    // so if is not an array, we must transform it and check after-wards (not lazy anymore)
    if (!Array.isArray(value)) {
      value = await this.extractor.transform(value, this.resolver, true);
      if (value instanceof JsonElementStreamer) {
        return value;
      }
      transformed = true;
    }
    // check if initially or after transformation we got an array
    if (Array.isArray(value)) {
      return JsonElementStreamer.fromJsonArray(this, value, transformed);
    }
    return null;
  }

  public async transform(definition: any, allowReturningStreams: boolean = false) {
    return await this.extractor.transform(definition, this.resolver, allowReturningStreams);
  }

  public async transformItem(definition: any, current: any, index?: number, additionalName?: string, additional?: any) {
    const currentContext = getDocumentContext(current);
    let itemResolver: ParameterResolver;
    if (typeof index !== "number") {
      itemResolver = {
        get: name =>
          FunctionContext.pathOfVar(FunctionContext.DOUBLE_HASH_CURRENT, name)
            ? currentContext.read(FunctionContext.DOLLAR + name.substring(9))
            : this.resolver.get(name),
      };
    } else if (!additionalName) {
      itemResolver = {
        get: name =>
          name === FunctionContext.DOUBLE_HASH_INDEX
            ? index
            : FunctionContext.pathOfVar(FunctionContext.DOUBLE_HASH_CURRENT, name)
              ? currentContext.read(FunctionContext.DOLLAR + name.substring(9))
              : this.resolver.get(name),
      };
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
                : this.resolver.get(name),
      };
    }
    return this.extractor.transform(definition, itemResolver, false);
  }
}

export default FunctionContext;
