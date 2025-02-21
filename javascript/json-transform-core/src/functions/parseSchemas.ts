import { JSONSchemaUtils, TypeSchema } from "@nlighten/json-schema-utils";
import { FunctionDescriptor } from "./types";

function parseSchemas<T extends string>(
  functions: Record<T, FunctionDescriptor>,
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
    if (Array.isArray(func.overrides)) {
      for (let i = 0; i < func.overrides.length; i++) {
        const schemaOverride = func.overrides[i].then.outputSchema;
        func.overrides[i] = {
          ...func,
          overrides: null,
          parsedOutputSchema: schemaOverride && JSONSchemaUtils.parse(schemaOverride),
        };
      }
    }
  }
  return functions;
}

export default parseSchemas;
