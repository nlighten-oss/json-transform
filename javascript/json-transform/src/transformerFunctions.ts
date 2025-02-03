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
  private static readonly inlineFunctionRegex = /^\$\$(\w+)(\((.*?)\))?(:|$)/;
  private static readonly inlineFunctionArgsRegex = /('(\\'|[^'])*'|[^,]*)(?:,|$)/g;
  public static readonly FUNCTION_KEY_PREFIX = "$$";
  public static readonly QUOTE_APOS = "'";
  public static readonly ESCAPE_DOLLAR = "\\$";
  public static readonly ESCAPE_HASH = "\\#";

  private functions: Record<string, TransformerFunction> = {};

  constructor() {
    this.registerFunctions(embeddedFunctions);
  }

  public registerFunctions(moreFunctions: Record<string, TransformerFunction>) {
    const additions = Object.entries(moreFunctions).filter(([key, value]) => {
      if (Object.prototype.hasOwnProperty.call(this.functions, key)) {
        console.debug(`Skipping registering function $$${key} (already exists)`);
        return false;
      }
      return true;
    });
    if (additions.length) {
      console.debug(`Registering functions: $$${additions.map(([key, _]) => key).join(", $$")}`);
      additions.forEach(add => {
        this.functions[add[0]] = add[1];
      });
    }
  }

  async matchObject(path: string, definition: any, resolver: ParameterResolver, transformer: JsonTransformerFunction) {
    if (isNullOrUndefined(definition)) {
      return null;
    }
    // look for an object function
    // (precedence is all internal functions sorted alphabetically first, then client added ones second, by registration order)
    for (const key in this.functions) {
      if (Object.prototype.hasOwnProperty.call(definition, TransformerFunctions.FUNCTION_KEY_PREFIX + key)) {
        const func = this.functions[key];
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
    const match = value.match(TransformerFunctions.inlineFunctionRegex);
    if (match) {
      const functionKey = match[1];
      if (this.functions[functionKey]) {
        const _function = this.functions[functionKey];
        const argsString = match[3];
        const args: (string | null | undefined)[] = [];
        if (argsString) {
          const argMatcher = argsString.matchAll(TransformerFunctions.inlineFunctionArgsRegex);
          for (const argMatch of argMatcher) {
            if (argMatch.index != argsString.length || argsString.endsWith(",")) {
              let arg = argMatch[1];
              let trimmed = arg?.trim();
              if (
                trimmed?.startsWith(TransformerFunctions.QUOTE_APOS) &&
                trimmed.endsWith(TransformerFunctions.QUOTE_APOS) &&
                trimmed.length > 1
              ) {
                arg = `${singleQuotedStringJsonParse(trimmed)}`;
              }
              args.push(arg);
            }
          }
        }
        var matchEndIndex = (match?.index ?? 0) + match[0].length;
        let input: null | string;
        if (value.charAt(matchEndIndex - 1) != ":") {
          // if not ends with ':' then no input value specified
          input = null;
        } else {
          input = value.substring(matchEndIndex);
        }
        return InlineFunctionContext.create(
          path + "/" + TransformerFunctions.FUNCTION_KEY_PREFIX + functionKey,
          input,
          args,
          functionKey,
          _function,
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
      const func = this.functions[context.getAlias()];
      const result = await func.apply(context);
      return new FunctionMatchResult(result, resolvedPath);
    } catch (ex) {
      console.warn(`Failed running inline function  (at ${resolvedPath})`, ex);
    }
    return new FunctionMatchResult(null, resolvedPath);
  }
}

export default new TransformerFunctions();
