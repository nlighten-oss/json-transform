import { TypeSchema } from "@nlighten/json-schema-utils";

const JsonPathFunctionsResultSchemas: Record<string, TypeSchema> = {
  // fallback type is string (no format)
  min: { type: "number" },
  max: { type: "number" },
  avg: { type: "number" },
  stddev: { type: "number" },
  length: { type: "integer" },
  sum: { type: "number" },
  keys: { type: "array", items: { type: "string" } },
  concat: { type: "string" },
  append: { type: "array" },
};

export const JsonPathFunctionRegex = new RegExp(`\\.(${Object.keys(JsonPathFunctionsResultSchemas).join("|")})\\(\\)?`);

export const matchJsonPathFunction = (data: any) => {
  if (typeof data === "undefined" || data === null) return null;
  const match = data.toString().match(JsonPathFunctionRegex);
  if (!match) return null;
  return JsonPathFunctionsResultSchemas[match[1]];
};
