import { TypeSchema } from "@nlighten/json-schema-utils";
import { ContextVariablesSchemas } from "./functions/ContextVariablesSchemas";

class TransformUtils {
  private additionalContext: Set<string>;
  private specialKeys: Set<string>;
  private contextVariablesSchemas: Record<string, TypeSchema>;
  /**
   * null means that the schema is defined at runtime
   */
  private scopedContextVariablesSchema: Record<string, TypeSchema | null>;
  private allContextVariables: string[];
  public variableDetectionRegExp: RegExp;

  constructor(
    additionalContext?: Set<string>,
    specialKeys?: Set<string>,
    contextVariablesSchemas?: Record<string, TypeSchema>,
    scopedContextVariablesSchema?: Record<string, TypeSchema>,
  ) {
    this.additionalContext = additionalContext || new Set();
    this.variableDetectionRegExp = this.variableDetectionRegExpFactory();
    this.specialKeys = specialKeys || new Set();
    this.contextVariablesSchemas = contextVariablesSchemas
      ? Object.assign({}, ContextVariablesSchemas, contextVariablesSchemas)
      : ContextVariablesSchemas;
    this.scopedContextVariablesSchema = scopedContextVariablesSchema || {};
    this.allContextVariables = Object.keys(this.contextVariablesSchemas).concat(
      Object.keys(this.scopedContextVariablesSchema),
    );
  }

  /**
   * This affects detection and syntax highlighting of additional context variables (e.g. $varname)
   * @param additionalContext
   */
  public setAdditionalContext(additionalContext: Set<string>) {
    this.additionalContext = additionalContext;
    this.variableDetectionRegExp = this.variableDetectionRegExpFactory();
  }

  public getAdditionalContext() {
    return this.additionalContext;
  }

  /**
   * This mainly affects syntax highlighting. Making certain keys being highlighted differently.
   */
  public setSpecialKeys(specialKeys: Set<string>) {
    this.specialKeys = specialKeys;
  }

  public getSpecialKeys() {
    return this.specialKeys;
  }

  /**
   * This affects detection and syntax highlighting of additional context variables (e.g. #varname)
   * In this case, a schema is also required
   * @param contextVariablesSchemas
   */
  public setContextVariablesSchemas(contextVariablesSchemas: Record<string, TypeSchema>) {
    this.contextVariablesSchemas = Object.assign({}, ContextVariablesSchemas, contextVariablesSchemas);
    this.allContextVariables = Object.keys(this.contextVariablesSchemas).concat(
      Object.keys(this.scopedContextVariablesSchema),
    );
  }

  public getContextVariablesSchemas() {
    return this.contextVariablesSchemas;
  }

  public setScopedContextVariablesSchema(scopedContextVariablesSchema: Record<string, TypeSchema | null>) {
    this.scopedContextVariablesSchema = scopedContextVariablesSchema;
    this.allContextVariables = Object.keys(this.contextVariablesSchemas).concat(
      Object.keys(this.scopedContextVariablesSchema),
    );
  }

  public getScopedContextVariablesSchema() {
    return this.scopedContextVariablesSchema;
  }

  public matchesAnyOfContextVariables(variableName: string) {
    for (const key of this.allContextVariables) {
      if (variableName === key || variableName.startsWith(key + ".") || variableName.startsWith(key + "[")) {
        return true;
      }
    }
    return false;
  }

  public matchesAnyOfAdditionalContext(variableName: string) {
    for (const key of this.additionalContext) {
      if (variableName === key || variableName.startsWith(key + ".") || variableName.startsWith(key + "[")) {
        return true;
      }
    }
    return false;
  }

  public matchesAnyOfSpecialKeys(variableName: string) {
    for (const key of this.specialKeys) {
      if (variableName === key || variableName.startsWith(key + ".") || variableName.startsWith(key + "[")) {
        return true;
      }
    }
    return false;
  }

  private variableDetectionRegExpFactory(flags = "g") {
    const altNames: string[] = [];
    for (const key of this.additionalContext) {
      altNames.push(key.substring(1));
    }
    const altPrefixes = altNames.length ? `(${altNames.join("|")})?` : "";

    return new RegExp(
      `(?<![\\])}?!@#$%^&*+\\\\\\w])(#[a-z_]+[a-z_\\d]*|\\$(?!\\$)${altPrefixes})(((\\.(?![-\\w$]+\\()[-\\w$]+)|(\\[[^\\]\\n]+]))+|(?=[^\\w.]|$))`,
      flags,
    );
  }
}

export const transformUtils = new TransformUtils();
