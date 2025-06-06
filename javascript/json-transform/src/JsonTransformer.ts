import { Transformer } from "./Transformer";
import { JsonTransformerFunction } from "./JsonTransformerFunction";
import { ParameterResolver } from "./ParameterResolver";
import { createPayloadResolver, isNullOrUndefined, toObjectFieldPath } from "./JsonHelpers";
import transformerFunctions, { TransformerFunctionsAdapter } from "./TransformerFunctions";
import JsonElementStreamer from "./JsonElementStreamer";
import BigNumber from "bignumber.js";
import { BigDecimal } from "./functions/common/FunctionHelpers";

export class JsonTransformer implements Transformer {
  static readonly OBJ_DESTRUCT_KEY = "*";
  static readonly NULL_VALUE = "#null";

  private transformerFunctions: TransformerFunctionsAdapter;
  private definition: any;
  private JSON_TRANSFORMER: JsonTransformerFunction;

  public constructor(definition: any, functionsAdapter?: TransformerFunctionsAdapter) {
    this.transformerFunctions = functionsAdapter ?? transformerFunctions;
    this.definition = definition;
    this.JSON_TRANSFORMER = {
      transform: this.fromJsonElement.bind(this),
    };
  }

  /**
   * Transforms the payload using the transformer definition
   *
   * @param payload               The payload to transform
   * @param additionalContext     (optional) Additional context to use in the transformation
   * @returns A promise to the transformed payload
   */
  async transform(payload: any = null, additionalContext: Record<string, any> = {}) {
    if (isNullOrUndefined(this.definition)) {
      return null;
    }
    const resolver: ParameterResolver = createPayloadResolver(payload, additionalContext);
    return this.fromJsonElement("$", this.definition, resolver, false);
  }

  protected async fromJsonPrimitive(
    path: string,
    definition: any,
    resolver: ParameterResolver,
    allowReturningStreams: boolean,
  ): Promise<any> {
    if (typeof definition !== "string") {
      return definition ?? null;
    }
    try {
      // test for inline function (e.g. $$function:...)
      const match = await this.transformerFunctions.matchInline(path, definition, resolver, this.JSON_TRANSFORMER);
      if (match != null) {
        const matchResult = match.getResult();
        if (matchResult instanceof JsonElementStreamer) {
          return allowReturningStreams ? matchResult : await matchResult.toJsonArray();
        }
        return match.getResult();
      }
      // jsonpath / context
      return resolver.get(definition);
    } catch (ignored: any) {
      // console.warn(`Failed getting value for ${definition}`, ignored);
      return null;
    }
  }

  protected async fromJsonObject(
    path: string,
    definition: any,
    resolver: ParameterResolver,
    allowReturningStreams: boolean,
  ): Promise<any> {
    const match = await this.transformerFunctions.matchObject(path, definition, resolver, this.JSON_TRANSFORMER);
    if (match != null) {
      const res = match.getResult();
      if (res instanceof JsonElementStreamer) {
        return allowReturningStreams ? res : await res.toJsonArray();
      }
      return match.getResult();
    }

    let result: Record<string, any> = {};
    var hasObjectDestruction = Object.prototype.hasOwnProperty.call(definition, JsonTransformer.OBJ_DESTRUCT_KEY);
    if (hasObjectDestruction) {
      const val = definition[JsonTransformer.OBJ_DESTRUCT_KEY];
      const res = await this.fromJsonElement(path + '["*"]', val, resolver, false);
      if (res != null) {
        const isArray = Array.isArray(val);
        if (isArray && Array.isArray(res)) {
          for (let i = 0; i < res.length; i++) {
            if (typeof res[i] === "object") {
              Object.assign(result, res[i]);
            }
          }
        } else if (typeof res === "object") {
          result = res;
        } else {
          result[JsonTransformer.OBJ_DESTRUCT_KEY] = res;
        }
      }
    }

    for (const key in definition) {
      if (key === JsonTransformer.OBJ_DESTRUCT_KEY) continue;
      const localValue = definition[key];
      if (localValue === JsonTransformer.NULL_VALUE) {
        if (hasObjectDestruction) {
          // don't define key if #null was used
          // might already exist, so try removing it
          delete result[key];
        } else {
          result[key] = null;
        }
        continue;
      }
      const value = await this.fromJsonElement(path + toObjectFieldPath(key), localValue, resolver, false);
      if (
        !isNullOrUndefined(value) ||
        Object.prototype.hasOwnProperty.call(result, key) /* we allow overriding with null*/
      ) {
        result[key] = value;
      }
    }

    return result;
  }

  protected async fromJsonElement(
    path: string,
    definition: any,
    resolver: ParameterResolver,
    allowReturningStreams: boolean,
  ): Promise<any> {
    if (isNullOrUndefined(definition)) {
      return null;
    }
    if (Array.isArray(definition)) {
      return Promise.all(definition.map((d: any, i) => this.fromJsonElement(`${path}[${i}]`, d, resolver, false)));
    }
    if (typeof definition === "object" && !(definition instanceof BigNumber || definition instanceof BigDecimal)) {
      return this.fromJsonObject(path, definition, resolver, allowReturningStreams);
    }
    return this.fromJsonPrimitive(path, definition, resolver, allowReturningStreams);
  }

  /**
   * Gets the transformer definition
   */
  getDefinition() {
    return this.definition;
  }
}
