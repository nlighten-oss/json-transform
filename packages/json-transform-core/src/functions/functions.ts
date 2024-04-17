import { Argument, EmbeddedTransformerFunction, EmbeddedTransformerFunctions, FunctionDescriptor } from "./types";
import parseSchemas from "./parseSchemas";
import { embeddedFunctions } from "./embeddedFunctions";
import { HandleFunctionMethod } from "../ParseContext";

type ObjectFunctionMatchResult = {
  name: string;
  func: FunctionDescriptor;
  value: any;
  spec: any;
};

function compareArgumentPosition(a: Argument, b: Argument) {
  return (a.position ?? Infinity) - (b.position ?? Infinity);
}
export const getFunctionInlineSignature = (name: string, func: FunctionDescriptor, requiredOnly?: boolean) => {
  return (
    "$$" +
    name +
    (func.arguments?.some(a => a.position === 0 && a.required)
      ? "(" +
        func.arguments
          .filter(a => typeof a.position === "number" && (!requiredOnly || a.required))
          .sort(compareArgumentPosition)
          .map(a => (a.required ? "{" + a.name + "}" : "[" + a.name + "]"))
          .join(",") +
        ")"
      : "")
  );
};

export const getFunctionObjectSignature = (name: string, func: FunctionDescriptor) => {
  return `{ "\\$\\$${name}": {input} ${
    func.arguments?.some(a => a.position === 0 && a.required)
      ? "," +
        func.arguments
          .filter(a => typeof a.position === "number" && a.required)
          .sort(compareArgumentPosition)
          .map(a => `"${a.name}": {${a.name}}`)
          .join(", ")
      : ""
  }}`;
};

class Functions {
  private clientFunctions: Record<string, FunctionDescriptor>;
  private allFunctionsNames: string[];
  private objectFunctionRegex: RegExp;
  private inlineFunctionRegex: RegExp;
  private matchInlineFunctionWithArgsRegex: RegExp;
  private clientDocsUrlResolver: ((funcName: string) => string) | undefined;
  public handleClientFunction?: HandleFunctionMethod;

  /**
   * Match Groups: DO NOT CHANGE
   * 0 - Everything
   * 1 - Function name (no $$)
   * 2 - Args with parenthesis
   * 3 - Args without parenthesis
   * 4 - Colon (or something else)
   * 5 - What comes after the colon (value) -- only when 'noArgs' = false
   */
  private inlineFunctionRegexFactory = (global: boolean, noArgs: boolean) =>
    new RegExp(
      `\\$\\$(${this.getNames().join("|")})(\\((.*?)\\))?(:${noArgs ? "" : `([^"]*)`}|$|")`,
      global ? "g" : undefined,
    );

  private objectFunctionRegexFactory = () => new RegExp(`(?<=")\\$\\$(${this.getNames().join("|")})":`, "g");

  constructor() {
    this.clientFunctions = {};
    this.allFunctionsNames = EmbeddedTransformerFunctions;

    this.objectFunctionRegex = this.objectFunctionRegexFactory();
    this.inlineFunctionRegex = this.inlineFunctionRegexFactory(true, true);
    this.matchInlineFunctionWithArgsRegex = this.inlineFunctionRegexFactory(false, false);
  }

  setClientFunctions(
    clientFunctions: Record<string, FunctionDescriptor>,
    handler?: HandleFunctionMethod,
    docsUrlResolver?: (funcName: string) => string,
  ) {
    this.clientFunctions = parseSchemas(clientFunctions, true);
    this.clientDocsUrlResolver = docsUrlResolver;
    this.allFunctionsNames = Object.keys(clientFunctions).concat(EmbeddedTransformerFunctions).sort();

    this.objectFunctionRegex = this.objectFunctionRegexFactory();
    this.inlineFunctionRegex = this.inlineFunctionRegexFactory(true, true);
    this.matchInlineFunctionWithArgsRegex = this.inlineFunctionRegexFactory(false, false);
    this.handleClientFunction = handler;
  }

  get(name: string) {
    return this.clientFunctions?.[name] ?? embeddedFunctions[name as EmbeddedTransformerFunction];
  }

  getNames() {
    return this.allFunctionsNames;
  }

  resolveDocsUrl(funcName: string, functionDescriptor?: FunctionDescriptor) {
    const name = functionDescriptor?.aliasTo ?? funcName;
    if (functionDescriptor?.custom && this.clientDocsUrlResolver) {
      const url = this.clientDocsUrlResolver(name);
      if (url) {
        return url;
      }
    }
    return `https://nlighten-oss.github.io/json-transform/functions/${name}`;
  }

  matchInline(
    data: any,
    callback?: (
      funcName: EmbeddedTransformerFunction,
      func: FunctionDescriptor,
      value: any,
      args: Record<string, any>,
    ) => any,
  ) {
    if (typeof data !== "string") return null;

    const m = data.match(this.matchInlineFunctionWithArgsRegex);
    if (!m) {
      return null;
    }
    const funcName = m[1] as EmbeddedTransformerFunction;
    let func = functions.get(funcName);
    if (!func) return null;
    if (func.argBased || callback) {
      const argsWithoutParenthesis = m[3];
      const args = parseArgs(func, argsWithoutParenthesis);
      if (func.argBased) {
        func = func.argBased(args); // replace func instance based on args
      }
      if (callback) {
        const inlineFunctionValue = m[5];
        callback(funcName, func, inlineFunctionValue, args);
      }
    }
    return func;
  }

  matchObject(data: any, extractOutputTypeRecursively?: boolean): ObjectFunctionMatchResult | undefined {
    if (!data || typeof data !== "object") return;
    for (const funcName of this.allFunctionsNames) {
      const key = "$$" + funcName;
      const value = data[key];
      if (typeof value !== "undefined") {
        if (extractOutputTypeRecursively && this.get(funcName).pipedType) {
          const match = this.matchObject(value, true);
          if (match) return match;
        }
        const fd = this.get(funcName);
        return {
          name: funcName,
          func: fd.argBased?.call(fd, data) ?? fd,
          value,
          spec: data,
        };
      }
    }
  }

  matchAllObjectFunctionsInLine(line: string) {
    return line.matchAll(this.objectFunctionRegex);
  }
  matchAllInlineFunctionsInLine(line: string) {
    return line.matchAll(this.inlineFunctionRegex);
  }
}

export const functions = new Functions();

export const parseArgs = (func: FunctionDescriptor, args?: string) => {
  if (!args) return {};
  const argsValues = args.split(/,(?=(?:[^']*'[^']*')*[^']*$)/).map((argVal: string) => {
    let arg = argVal;
    const trimmed = argVal.trim();
    if (trimmed.startsWith("'") && trimmed.endsWith("'") && trimmed.length > 1) {
      arg = trimmed.substring(1, trimmed.length - 1).replace(/''/g, "'");
      //otherwise, take the whole argument as-is
    }
    return arg;
  });
  return !argsValues || argsValues.length === 0
    ? {}
    : argsValues.reduce(
        (a, c, i) => {
          func.arguments?.forEach(fa => {
            if (fa.position === i && fa.name) {
              a[fa.name] = c;
            }
          });
          return a;
        },
        {} as Record<string, any>, // we can't really infer type, and strings are the only ones that matter anyway
      );
};
