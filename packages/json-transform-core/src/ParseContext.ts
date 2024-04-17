import { TypeSchema } from "@nlighten/json-schema-utils";
import { ContextVariablesSchemas } from "./functions/context";
import { FunctionDescriptor } from "./functions/types";

export class ParseContext {
  paths?: Record<string, TypeSchema> = {};
  additionalContext?: Record<string, TypeSchema>;

  constructor(paths?: Record<string, TypeSchema>, additionalContext?: Record<string, TypeSchema>) {
    this.paths = paths;
    this.additionalContext = additionalContext ?? ContextVariablesSchemas;
  }

  resolve(key: string) {
    return this.additionalContext?.[key] ?? this.paths?.[key];
  }
}

export type ParseMethod = (
  definition: any,
  targetPath: string,
  localPath: string,
  previousPaths: string[],
  paths: string[],
  context: ParseContext,
) => void;

export type HandleFunctionMethod = (
  detectedAs: "inline" | "object",
  funcName: string,
  func: FunctionDescriptor,
  value: any,
  args: Record<string, any>,
  targetPath: string,
  localPath: string,
  previousPaths: string[],
  paths: string[],
  context: ParseContext,
  parse?: ParseMethod,
) => boolean;

export default ParseContext;
