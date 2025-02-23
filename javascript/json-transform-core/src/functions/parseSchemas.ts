import { JSONSchemaUtils } from "@nlighten/json-schema-utils";
import { Argument, EmbeddedTransformerFunction, FunctionDescriptor } from "./types";

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

function parseSchemas<T extends string>(
  functions: Record<T, FunctionDescriptor>,
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
    if (Array.isArray(func.overrides)) {
      for (let i = 0; i < func.overrides.length; i++) {
        const schemaOverride = func.overrides[i].then.outputSchema;
        func.overrides[i] = {
          if: func.overrides[i].if,
          then: {
            ...func,
            ...func.overrides[i].then,
            overrides: undefined,
            parsedOutputSchema: schemaOverride && JSONSchemaUtils.parse(schemaOverride),
            defaultValues: getDefaultValues(func.overrides[i].then.arguments),
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

export default parseSchemas;
