import { tryConvertFunctionsToInline } from "./utils/convert";

export { ContextVariablesSchemas } from "./functions/ContextVariablesSchemas";
export {
  getFunctionInlineSignature,
  getFunctionObjectSignature,
  functionsParser,
  parseArgs,
  getSubfunction,
} from "./functions/functionsParser";
export { convertFunctionsToObjects, tryConvertFunctionsToInline } from "./utils/convert";
export {
  type Argument,
  type ArgumentCondition,
  type ConditionalSubFunction,
  type FunctionDefinition,
  type FunctionDescriptor,
  EmbeddedTransformerFunction,
  EmbeddedTransformerFunctions,
  type JsonTransformExample,
} from "./functions/types";

export { jsonpathJoin } from "./jsonpath/jsonpathJoin";
export { JsonPathFunctionRegex } from "./jsonpath/jsonpathFunctions";

export { parseTransformer } from "./parse";
export { ParseContext, type ParseMethod, type HandleFunctionMethod } from "./ParseContext";

import definitions from "./functions/definitions";
import examples from "./functions/examples";
import functions from "./functions/functions";
export { definitions, examples, functions };

export { transformUtils } from "./transformUtils";
