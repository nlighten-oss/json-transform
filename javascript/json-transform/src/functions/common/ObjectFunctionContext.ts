import FunctionContext from "./FunctionContext";
import {isNullOrUndefined} from "../../JsonHelpers";

class ObjectFunctionContext extends FunctionContext {
  private definition: any;
  constructor(definition: any, functionKey: string, func: any, resolver: any, extractor: any) {
    super(functionKey, func, resolver, extractor, definition);
    this.definition = definition;
  }

  override has(name: string): boolean {
    return Object.prototype.hasOwnProperty.call(this.definition, name);
  }

  override get(name: string, transform: boolean = true): any {
    var el = this.definition[name == null ? this.alias : name];
    if (isNullOrUndefined(el)) {
      return this.function.getDefaultValue(name);
    }
    return transform ? this.extractor.transform(el, this.resolver, true) : el;
  }
}

export default ObjectFunctionContext;