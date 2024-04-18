import { JSONSchemaUtils } from "@nlighten/json-schema-utils";
import { FunctionDescriptor } from "./types";

function parseSchemas<F extends Record<T, FunctionDescriptor>, T extends string>(
  functions: F,
  custom?: boolean,
): Record<T, FunctionDescriptor> {
  for (const f in functions) {
    const func = functions[f];
    if (func.outputSchema) {
      func.parsedOutputSchema = JSONSchemaUtils.parse(func.outputSchema);
    }
    if (custom) {
      func.custom = true;
    }
  }
  return functions;
}

export default parseSchemas;
