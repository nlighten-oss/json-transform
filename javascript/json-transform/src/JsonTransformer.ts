import {Transformer} from "./Transformer";
import {JsonTransformerFunction} from "./JsonTransformerFunction";
import {ParameterResolver} from "./ParameterResolver";
import {createPayloadResolver, isNullOrUndefined} from "./JsonHelpers";
import transformerFunctions, { TransformerFunctions } from "./transformerFunctions";

class JsonTransformer implements Transformer {

  static readonly OBJ_DESTRUCT_KEY = "*";
  static readonly FUNCTION_PREFIX = "$$";

  private transformerFunctions: TransformerFunctions;
  private definition: any;
  private JSON_TRANSFORMER: JsonTransformerFunction;

  constructor(definition: any) {
    this.transformerFunctions = transformerFunctions;
    this.definition = definition;
    this.JSON_TRANSFORMER = {
      transform: this.fromJsonElement.bind(this)
    };
  }


  transform(payload: any = null, additionalContext: Record<string, any> = {}) {
    if (isNullOrUndefined(this.definition)) {
      return null;
    }
    const resolver : ParameterResolver = createPayloadResolver(payload, additionalContext)
    return this.fromJsonElement(this.definition, resolver, false);
  }

  fromJsonPrimitive(definition: any, resolver: ParameterResolver, allowReturningStreams: boolean) : any {
    if (typeof definition !== 'string') {
      return definition;
    }
    try {
      // test for inline function (e.g. $$function:...)
      const match = this.transformerFunctions.matchInline(definition, resolver, this.JSON_TRANSFORMER);
      if (match != null) {
        // TODO: add streams support
        return match.getResult();
      }
      // jsonpath / context
      return resolver.get(definition);
    } catch (ignored: any) {
      // console.warn(`Failed getting value for ${definition}`, ignored);
      return null;
    }
  }

  fromJsonObject(definition: any, resolver: ParameterResolver, allowReturningStreams: boolean) : any {
    const match = this.transformerFunctions.matchObject(definition, resolver, this.JSON_TRANSFORMER);
    if (match != null) {
      // TODO: add streams support
      return match.getResult();
    }

    let result: Record<string, any> = {};
    if (Object.prototype.hasOwnProperty.call(definition, JsonTransformer.OBJ_DESTRUCT_KEY)) {
      const val = definition[JsonTransformer.OBJ_DESTRUCT_KEY];
      const res = this.fromJsonElement(val, resolver, false);
      if (res != null) {
        const isArray = Array.isArray(val);
        if (isArray && Array.isArray(res)) {
          for (let i = 0; i < res.length; i++) {
            if (typeof res[i] === 'object') {
              Object.assign(result, res[i]);
            }
          }
        } else if (typeof res === 'object') {
          result = res;
        } else {
          result[JsonTransformer.OBJ_DESTRUCT_KEY] = res;
        }
      }
    }

    for (const key in definition) {
      if (key === JsonTransformer.OBJ_DESTRUCT_KEY) continue;
      const value = this.fromJsonElement(definition[key], resolver, false);
      if (!isNullOrUndefined(value) || Object.prototype.hasOwnProperty.call(result, key) /* we allow overriding with null*/) {
        result[key] = value;
      }
    }

    return result;
  }

  fromJsonElement(definition: any, resolver: ParameterResolver, allowReturningStreams: boolean) : any {
    if (isNullOrUndefined(definition)) {
      return null;
    }
    if (Array.isArray(definition)) {
      return definition.map((d: any) => this.fromJsonElement(d, resolver, false));
    }
    if (typeof definition === 'object') {
      return this.fromJsonObject(definition, resolver, allowReturningStreams);
    }
    return this.fromJsonPrimitive(definition, resolver, allowReturningStreams);
  }

  getDefinition() {
    return this.definition;
  }
}

export default JsonTransformer;