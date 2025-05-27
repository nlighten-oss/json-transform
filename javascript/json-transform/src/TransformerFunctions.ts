import { tokenizeInlineFunction } from "@nlighten/json-transform-core";
import TransformerFunction from "./functions/common/TransformerFunction";
import { ParameterResolver } from "./ParameterResolver";
import { JsonTransformerFunction } from "./JsonTransformerFunction";
import { isNullOrUndefined, singleQuotedStringJsonParse } from "./JsonHelpers";
import ObjectFunctionContext from "./functions/common/ObjectFunctionContext";
import InlineFunctionContext from "./functions/common/InlineFunctionContext";
import embeddedFunctions from "./functions";

/**
 * The purpose of this class is to differentiate between null and null result
 */
export class FunctionMatchResult {
  private readonly result: any;
  private readonly resultPath: string;

  constructor(result: any, resultPath: string) {
    this.result = result;
    this.resultPath = resultPath;
  }

  getResult() {
    return this.result;
  }
  getResultPath() {
    return this.resultPath;
  }
}

export interface TransformerFunctionsAdapter {
  matchObject(
    path: string,
    definition: any,
    resolver: ParameterResolver,
    transformer: JsonTransformerFunction,
  ): Promise<FunctionMatchResult | null>;
  matchInline(
    path: string,
    value: string,
    resolver: ParameterResolver,
    transformer: JsonTransformerFunction,
  ): Promise<FunctionMatchResult | null>;
}

export class TransformerFunctions implements TransformerFunctionsAdapter {
  public static readonly FUNCTION_KEY_PREFIX = "$$";
  public static readonly QUOTE_APOS = "'";
  public static readonly ESCAPE_DOLLAR = "\\$";
  public static readonly ESCAPE_HASH = "\\#";

  private static readonly functions: Record<string, TransformerFunction> = {};

  static {
    TransformerFunctions.registerFunctions(embeddedFunctions);
  }

  public static registerFunctions(moreFunctions: Record<string, TransformerFunction>) {
    const additions = Object.entries(moreFunctions).filter(([key, value]) => {
      if (Object.prototype.hasOwnProperty.call(TransformerFunctions.functions, key)) {
        console.debug(`Skipping registering function $$${key} (already exists)`);
        return false;
      }
      return true;
    });
    if (additions.length) {
      console.debug(`Registering functions: $$${additions.map(([key, _]) => key).join(", $$")}`);
      additions.forEach(add => {
        TransformerFunctions.functions[add[0]] = add[1];
      });
    }
  }

  async matchObject(path: string, definition: any, resolver: ParameterResolver, transformer: JsonTransformerFunction) {
    if (isNullOrUndefined(definition)) {
      return null;
    }
    // look for an object function
    // (precedence is all internal functions sorted alphabetically first, then client added ones second, by registration order)
    for (const key in TransformerFunctions.functions) {
      if (Object.prototype.hasOwnProperty.call(definition, TransformerFunctions.FUNCTION_KEY_PREFIX + key)) {
        const func = TransformerFunctions.functions[key];
        const context = await ObjectFunctionContext.createAsync(
          path,
          definition,
          TransformerFunctions.FUNCTION_KEY_PREFIX + key,
          func,
          resolver,
          transformer,
        );
        const resolvedPath = path + "." + TransformerFunctions.FUNCTION_KEY_PREFIX + key;
        try {
          const result = await func.apply(context);
          return new FunctionMatchResult(result, resolvedPath);
        } catch (ex) {
          console.warn(`Failed running object function (at ${resolvedPath})`, ex);
          return new FunctionMatchResult(null, resolvedPath);
        }
      }
    }
    // didn't find an object function
    return null;
  }

  tryParseInlineFunction(
    path: string,
    value: string,
    resolver: ParameterResolver,
    transformer: JsonTransformerFunction,
  ) {
    const match = tokenizeInlineFunction(value);
    if (match) {
      const functionKey = match.name;
      if (TransformerFunctions.functions[functionKey]) {
        const args = match.args?.map(x => x.value) ?? [];
        for (let i = args.length - 1; i >= 0; i--) {
          if (args[i] !== null && typeof args[i] !== "undefined") {
            break;
          }
          args.pop();
        }

        return InlineFunctionContext.create(
          path + "/" + TransformerFunctions.FUNCTION_KEY_PREFIX + functionKey,
          match.input?.value ?? null,
          args,
          functionKey,
          TransformerFunctions.functions[functionKey],
          resolver,
          transformer,
        );
      }
    }
    return null;
  }

  async matchInline(path: string, value: string, resolver: ParameterResolver, transformer: JsonTransformerFunction) {
    if (value == null) return null;
    const context = this.tryParseInlineFunction(path, value, resolver, transformer);
    if (context == null) {
      return null;
    }
    // at this point we detected an inline function, we must return a match result
    const resolvedPath = context.getPathFor(null);
    try {
      const func = TransformerFunctions.functions[context.getAlias()];
      const result = await func.apply(context);
      return new FunctionMatchResult(result, resolvedPath);
    } catch (ex) {
      console.warn(`Failed running inline function  (at ${resolvedPath})`, ex);
    }
    return new FunctionMatchResult(null, resolvedPath);
  }
}

export default new TransformerFunctions();
