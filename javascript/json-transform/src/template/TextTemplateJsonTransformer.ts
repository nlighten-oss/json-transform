import { JsonTransformer } from "../JsonTransformer";
import { ParameterResolver } from "../ParameterResolver";
import { TextTemplateJsonTransformFunction } from "./TextTemplateJsonTransformFunction";

class TextTemplateJsonTransformer extends JsonTransformer {
  constructor() {
    super(null);
  }
  public transformString(definition: any, resolver: ParameterResolver) {
    return super.fromJsonPrimitive("$", definition, resolver, false);
  }
}

const ttjt = new TextTemplateJsonTransformer();
export const TextTemplateJsonTransformFunctionInstance: TextTemplateJsonTransformFunction =
  ttjt.transformString.bind(ttjt);
