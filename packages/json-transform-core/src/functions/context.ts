import {TypeSchema} from "@nlighten/json-schema-utils";

export const ContextVariablesSchemas: Record<string, TypeSchema> = {
  "#uuid": { type: "string", format: "uuid" },
  "#null": { type: "null" },
  "#now": { type: "string", format: "date-time" },
};