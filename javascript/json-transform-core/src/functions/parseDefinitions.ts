import { JSONSchemaUtils } from "@nlighten/json-schema-utils";
import { Argument, EmbeddedTransformerFunction, FunctionDefinition, FunctionDescriptor } from "./types";

const getDefaultValues = (args?: Argument[]) => {
  const defaultValues: Record<string, any> = {};
  if (Array.isArray(args)) {
    for (let i = 0; i < args.length; i++) {
      const arg = args[i];
      if (arg.default !== undefined) {
        defaultValues[arg.name] = arg.default;
      }
    }
  }
  return defaultValues;
};

function parseDefinitions<T extends string>(
  functions: Record<T, FunctionDefinition>,
  custom?: boolean,
): Record<string, FunctionDescriptor> {
  const result = structuredClone(functions) as Record<string, FunctionDescriptor>;
  const aliasedFunctions: Record<string, FunctionDescriptor> = {};

  for (const f in functions) {
    const func = result[f];

    if (func.aliases) {
      for (let i = 0; i < func.aliases.length; i++) {
        const aliased = structuredClone(func);
        aliased.aliasTo = f;
        aliasedFunctions[func.aliases[i]] = aliased;
      }
    }
    if (func.outputSchema) {
      func.parsedOutputSchema = JSONSchemaUtils.parse(func.outputSchema);
    }
    func.defaultValues = getDefaultValues(func.arguments);

    if (custom) {
      func.custom = true;
    }
    if (Array.isArray(func.subfunctions)) {
      for (let i = 0; i < func.subfunctions.length; i++) {
        const schemaOverride = func.subfunctions[i].then.outputSchema ?? func.outputSchema;
        func.subfunctions[i] = {
          if: func.subfunctions[i].if,
          then: {
            outputSchema: func.outputSchema,
            ...func.subfunctions[i].then,
            parsedOutputSchema: schemaOverride && JSONSchemaUtils.parse(schemaOverride),
            defaultValues: getDefaultValues(func.subfunctions[i].then.arguments),
          },
        };
      }
    }
  }
  for (const f in aliasedFunctions) {
    result[f as EmbeddedTransformerFunction] = aliasedFunctions[f];
  }
  return result;
}

export default parseDefinitions;
