import FunctionContext from "./FunctionContext";
import { isMap, isNullOrUndefined, toObjectFieldPath } from "../../JsonHelpers";

class ObjectFunctionContext extends FunctionContext {
  private definition: any;
  private constructor(path: string, definition: any, functionKey: string, func: any, resolver: any, extractor: any) {
    super(path, functionKey, func, resolver, extractor);
    this.definition = definition;
  }

  public static async createAsync(
    path: string,
    definition: any,
    functionKey: string,
    func: any,
    resolver: any,
    extractor: any,
  ) {
    let objResolver = resolver;
    if (definition?.[FunctionContext.CONTEXT_KEY]) {
      const contextElement = definition[FunctionContext.CONTEXT_KEY];
      if (isMap(contextElement)) {
        objResolver = await FunctionContext.recalcResolver(path, contextElement, resolver, extractor);
      }
    }
    return new ObjectFunctionContext(path, definition, functionKey, func, objResolver, extractor);
  }

  override has(name: string): boolean {
    return Object.prototype.hasOwnProperty.call(this.definition, name);
  }

  override async get(name: string, transform: boolean = true): Promise<any> {
    const el = this.definition[name == null ? this.alias : name];
    if (isNullOrUndefined(el)) {
      return this.function.getDefaultValue(name);
    }
    return !transform ? el : await this.extractor.transform(this.getPathFor(name), el, this.resolver, true);
  }

  override getPathFor(key: number | string | null): string {
    return this.path + (typeof key === "number" ? `[${key}]` : toObjectFieldPath(!key ? this.getAlias() : key));
  }
}

export default ObjectFunctionContext;
