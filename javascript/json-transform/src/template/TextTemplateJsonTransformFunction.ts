import { ParameterResolver } from "../ParameterResolver";

export type TextTemplateJsonTransformFunction = (definition: any, resolver: ParameterResolver) => Promise<any>;

let current: TextTemplateJsonTransformFunction = async () => undefined;

export function setCurrentJsonTransformFunction(func: TextTemplateJsonTransformFunction) {
  current = func;
}

export function getCurrentJsonTransformFunction(): TextTemplateJsonTransformFunction {
  return current;
}
