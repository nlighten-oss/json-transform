import { isValidPropertyPath, TypeSchema } from "@nlighten/json-schema-utils";
import { ContextVariablesSchemas } from "./functions/context";
import { FunctionDescriptor } from "./functions/types";

export class ParseContext {
  paths?: Record<string, TypeSchema> = {};
  additionalContext?: Record<string, TypeSchema>;
  knownVariables: Set<string>;

  constructor(
    paths?: Record<string, TypeSchema>,
    additionalContext?: Record<string, TypeSchema>,
    previousPaths?: string[],
  ) {
    this.paths = paths;
    this.additionalContext = additionalContext ?? ContextVariablesSchemas;
    this.knownVariables = new Set();
    if (paths) {
      for (const path in paths) {
        const v = path.split(/[.[]/, 1)[0];
        this.knownVariables.add(v);
      }
    }
    if (additionalContext) {
      for (const path in additionalContext) {
        const v = path.split(/[.[]/, 1)[0];
        this.knownVariables.add(v);
      }
    }
    if (previousPaths) {
      for (const path of previousPaths) {
        const v = path.split(/[.[]/, 1)[0];
        this.knownVariables.add(v);
      }
    }
  }

  resolve(key: string) {
    return this.additionalContext?.[key] ?? this.paths?.[key];
  }

  isJsonPathReference(path: any): boolean {
    if (typeof path !== "string") return false;
    const v = path.split(/[.[]/, 1)[0];
    return path === v || path.startsWith(v + ".") || path.startsWith(v + "[");
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
