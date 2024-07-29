import TransformerFunction from "./functions/common/TransformerFunction";
import {ParameterResolver} from "./ParameterResolver";
import {JsonTransformerFunction} from "./JsonTransformerFunction";
import {isNullOrUndefined, lenientJsonParse} from "./JsonHelpers";
import ObjectFunctionContext from "./functions/common/ObjectFunctionContext";
import InlineFunctionContext from "./functions/common/InlineFunctionContext";
import TransformerFunctionAnd from "./functions/TransformerFunctionAnd";
import TransformerFunctionAt from "./functions/TransformerFunctionAt";
import TransformerFunctionAvg from "./functions/TransformerFunctionAvg";
import TransformerFunctionBase64 from "./functions/TransformerFunctionBase64";
import TransformerFunctionBoolean from "./functions/TransformerFunctionBoolean";

import TransformerFunctionLower from "./functions/TransformerFunctionLower";
import TransformerFunctionUpper from "./functions/TransformerFunctionUpper";
import TransformerFunctionIs from "./functions/TransformerFunctionIs";
import TransformerFunctionCoalesce from "./functions/TransformerFunctionCoalesce";
import TransformerFunctionConcat from "./functions/TransformerFunctionConcat";

class FunctionMatchResult {
  private result;

  constructor(result: any) {
    this.result = result;
  }

  getResult() {
    return this.result;
  }
}

const UNIMPLEMENTED = {
  aliases: ["unimplemented"],
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
    this.registerFunctions({
      "and": new TransformerFunctionAnd(),
      "at": new TransformerFunctionAt(),
      "avg": new TransformerFunctionAvg(),
      "base64": new TransformerFunctionBase64(),
      "boolean": new TransformerFunctionBoolean(),
      "coalesce": new TransformerFunctionCoalesce(),
      "concat": new TransformerFunctionConcat(),
      "contains": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionContains(),
      "csv": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionCsv(),
      "csvparse": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionCsvParse(),
      "date": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionDate(),
      "decimal": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionDecimal(),
      "digest": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionDigest(),
      "distinct": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionDistinct(),
      "entries": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionEntries(),
      "eval": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionEval(),
      "filter": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionFilter(),
      "find": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionFind(),
      "first": new TransformerFunctionCoalesce(), // * alias for coalesce
      "flat": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionFlat(),
      "flatten": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionFlatten(),
      "form": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionForm(),
      "formparse": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionFormParse(),
      "group": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionGroup(),
      "if": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionIf(),
      "is": new TransformerFunctionIs(),
      "isnull": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionIsNull(),
      "join": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionJoin(),
      "json": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionJsonParse(),
      "jsonparse": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionJsonParse(),
      "jsonpatch": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionJsonPatch(),
      "jsonpath": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionJsonPath(),
      "jsonpointer": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionJsonPointer(),
      "jwtparse": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionJwtParse(),
      "length": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionLength(),
      "long": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionLong(),
      "lookup": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionLookup(),
      "lower": new TransformerFunctionLower(),
      "map": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionMap(),
      "match": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionMatch(),
      "matchall": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionMatchAll(),
      "math": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionMath(),
      "max": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionMax(),
      "min": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionMin(),
      "normalize": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionNormalize(),
      "not": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionNot(),
      "numberformat": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionNumberFormat(),
      "numberparse": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionNumberParse(),
      "object": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionObject(),
      "or": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionOr(),
      "pad": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionPad(),
      "partition": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionPartition(),
      "range": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionRange(),
      "raw": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionRaw(),
      "reduce": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionReduce(),
      "replace": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionReplace(),
      "reverse": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionReverse(),
      "slice": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionSlice(),
      "sort": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionSort(),
      "split": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionSplit(),
      "string": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionString(),
      "substring": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionSubstring(),
      "sum": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionSum(),
      "switch": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionSwitch(),
      "test": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionTest(),
      "transform": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionTransform(),
      "trim": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionTrim(),
      "unflatten": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionUnflatten(),
      "upper": new TransformerFunctionUpper(),
      "uriparse": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionUriParse(),
      "urldecode": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionUrlDecode(),
      "urlencode": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionUrlEncode(),
      "uuid": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionUuid(),
      "value": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionValue(),
      "wrap": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionWrap(),
      "xml": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionXml(),
      "xmlparse": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionXmlParse(),
      "xor": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionXor(),
      "yaml": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionYaml(),
      "yamlparse": new TransformerFunction(UNIMPLEMENTED), // TODO: new TransformerFunctionYamlParse()
    });
  }

  public registerFunctions(moreFunctions: Record<string, TransformerFunction>) {
    const additions = Object.entries(moreFunctions)
      .filter(([key, value]) => {
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
      })
    }
  }

  matchObject(definition: any, resolver: ParameterResolver, transformer: JsonTransformerFunction) {
    if (isNullOrUndefined(definition)) {
      return null;
    }
    // look for an object function
    // (precedence is all internal functions sorted alphabetically first, then client added ones second, by registration order)
    for (const key in this.functions) {
      if (Object.prototype.hasOwnProperty.call(definition, TransformerFunctions.FUNCTION_KEY_PREFIX + key)) {
        const func = this.functions[key];
        const context = new ObjectFunctionContext(
          definition,
          TransformerFunctions.FUNCTION_KEY_PREFIX + key,
          func, resolver, transformer);
        try {
          return new FunctionMatchResult(func.apply(context));
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
        const _function= this.functions[functionKey];
        const argsString = match[3];
        const args: (string|null|undefined)[] = [];
        if (argsString) {
          const argMatcher = argsString.matchAll(TransformerFunctions.inlineFunctionArgsRegex);
          for (const argMatch of argMatcher) {
            if (argMatch.index != argsString.length || argsString.endsWith(",")) {
              let arg = argMatch[1];
              let trimmed = arg?.trim();
              // if after removing all the surrounding spaces we are left with quoted text, then unquote it
              if (trimmed?.startsWith(TransformerFunctions.QUOTE_APOS) && trimmed?.endsWith(TransformerFunctions.QUOTE_APOS) && trimmed.length > 1) {
                if (trimmed.startsWith(TransformerFunctions.ESCAPE_DOLLAR) || trimmed.startsWith(TransformerFunctions.ESCAPE_HASH)) {
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
        if (value.charAt(matchEndIndex - 1) != ':') { // if not ends with ':' then no input value specified
          input = null;
        } else {
          input = value.substring(matchEndIndex);
        }
        return new InlineFunctionContext(
          input, args,
          functionKey,
          _function,
          resolver, transformer);
      }
    }
    return null;
  }

  matchInline(value: string, resolver: ParameterResolver, transformer: JsonTransformerFunction) {
    if (value == null) return null;
    const context = this.tryParseInlineFunction(value, resolver, transformer);
    if (context == null) {
      return null;
    }
    // at this point we detected an inline function, we must return a match result
    try {
      const result = this.functions[context.getAlias()].apply(context);
      return new FunctionMatchResult(result);
    } catch (ex) {
      console.warn("Failed running inline function ", ex);
    }
    return new FunctionMatchResult(null);
  }
}

export default new TransformerFunctions();