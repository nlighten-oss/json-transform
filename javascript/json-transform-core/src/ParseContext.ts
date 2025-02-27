import { TypeSchema } from "@nlighten/json-schema-utils";
import { ContextVariablesSchemas } from "./functions/ContextVariablesSchemas";
import { FunctionDescriptor } from "./functions/types";

export class ParseContext {
  private readonly paths?: Record<string, TypeSchema>;
  private readonly additionalContext?: Record<string, TypeSchema>;
  private knownVariables: Set<string>;

  constructor(
    paths?: Record<string, TypeSchema>,
    additionalContext?: Record<string, TypeSchema>,
    previousPaths?: string[],
  ) {
    this.paths = paths;
    this.additionalContext = additionalContext;
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

  hasPaths() {
    return Boolean(this.paths);
  }

  hasPath(path: string) {
    return typeof this.paths?.[path] !== "undefined";
  }

  /**
   * If you are about to change the result, use this and not 'resolve()'
   * @param path
   */
  getPath(path: string) {
    return this.paths?.[path];
  }

  setPath(path: string, type: TypeSchema) {
    if (!this.paths) return;
    this.paths[path] = type;
    const v = path.split(/[.[]/, 1)[0];
    this.knownVariables.add(v);
  }

  removePaths(paths: string[], variableToRemove?: string) {
    for (const path of paths) {
      delete this.paths?.[path];
    }
    if (variableToRemove) {
      this.knownVariables.delete(variableToRemove);
    }
  }

  resolve(key: string) {
    return ContextVariablesSchemas[key] ?? this.additionalContext?.[key] ?? this.paths?.[key];
  }

  isReferencingKnownVariable(path: any): boolean {
    if (typeof path !== "string" || path.startsWith("$$")) return false;
    const v = path.split(/[.[]/, 1)[0];
    return this.knownVariables.has(v) && (path === v || path.startsWith(v + ".") || path.startsWith(v + "["));
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
