import TransformerFunction from "./functions/common/TransformerFunction";
import { ParameterResolver } from "./ParameterResolver";
import { JsonTransformerFunction } from "./JsonTransformerFunction";
import { isNullOrUndefined, lenientJsonParse } from "./JsonHelpers";
import ObjectFunctionContext from "./functions/common/ObjectFunctionContext";
import InlineFunctionContext from "./functions/common/InlineFunctionContext";
import embeddedFunctions from "./functions";

class FunctionMatchResult {
  private readonly result;

  constructor(result: any) {
    this.result = result;
  }

  getResult() {
    return this.result;
  }
}

export class TransformerFunctions {
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

  async matchObject(definition: any, resolver: ParameterResolver, transformer: JsonTransformerFunction) {
    if (isNullOrUndefined(definition)) {
      return null;
    }
    // look for an object function
    // (precedence is all internal functions sorted alphabetically first, then client added ones second, by registration order)
    for (const key in this.functions) {
      if (Object.prototype.hasOwnProperty.call(definition, TransformerFunctions.FUNCTION_KEY_PREFIX + key)) {
        const func = this.functions[key];
        const context = await ObjectFunctionContext.createAsync(
          definition,
          TransformerFunctions.FUNCTION_KEY_PREFIX + key,
          func,
          resolver,
          transformer,
        );
        try {
          const result = await func.apply(context);
          return new FunctionMatchResult(result);
        } catch (ex) {
          console.warn("Failed running object function ", ex);
          return new FunctionMatchResult(null);
        }
      }
    }
    // didn't find an object function
    return null;
  }

  tryParseInlineFunction(value: string, resolver: ParameterResolver, transformer: JsonTransformerFunction) {
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
              // if after removing all the surrounding spaces we are left with quoted text, then unquote it
              if (
                trimmed?.startsWith(TransformerFunctions.QUOTE_APOS) &&
                trimmed?.endsWith(TransformerFunctions.QUOTE_APOS) &&
                trimmed.length > 1
              ) {
                if (
                  trimmed.startsWith(TransformerFunctions.ESCAPE_DOLLAR) ||
                  trimmed.startsWith(TransformerFunctions.ESCAPE_HASH)
                ) {
                  trimmed = trimmed.substring(1); // escape
                }
                arg = `${lenientJsonParse(trimmed)}`;
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
        return InlineFunctionContext.create(input, args, functionKey, _function, resolver, transformer);
      }
    }
    return null;
  }

  async matchInline(value: string, resolver: ParameterResolver, transformer: JsonTransformerFunction) {
    if (value == null) return null;
    const context = this.tryParseInlineFunction(value, resolver, transformer);
    if (context == null) {
      return null;
    }
    // at this point we detected an inline function, we must return a match result
    try {
      const func = this.functions[context.getAlias()];
      const result = await func.apply(context);
      return new FunctionMatchResult(result);
    } catch (ex) {
      console.warn("Failed running inline function ", ex);
    }
    return new FunctionMatchResult(null);
  }
}

export default new TransformerFunctions();
