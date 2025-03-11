import { Argument, EmbeddedTransformerFunction, FunctionDefinition, FunctionDescriptor } from "./types";
import parseDefinitions from "./parseDefinitions";
import { HandleFunctionMethod } from "../ParseContext";
import functions from "./functions";

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

class FunctionsParser {
  private clientFunctions: Record<string, FunctionDescriptor>;
  private allFunctionsNames: Set<string>;
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
  private inlineFunctionRegexFactory = (names: string[], global: boolean, noArgs: boolean) =>
    new RegExp(
      `\\$\\$(${Array.from(this.getNames()).join("|")})(\\((.*?)\\))?(:${noArgs ? "" : `([^"]*)`}|$|")`,
      global ? "g" : undefined,
    );

  private objectFunctionRegexFactory = (names: string[]) => new RegExp(`(?<=")\\$\\$(${names.join("|")})":`, "g");

  constructor() {
    this.clientFunctions = {};
    const functionNames = new Set(Object.keys(functions));
    // add aliases
    for (const name of functionNames) {
      if (functions[name].aliases) {
        functions[name].aliases?.forEach(alias => functionNames.add(alias));
      }
    }
    this.allFunctionsNames = functionNames;
    const names = Array.from(this.allFunctionsNames);
    this.objectFunctionRegex = this.objectFunctionRegexFactory(names);
    this.inlineFunctionRegex = this.inlineFunctionRegexFactory(names, true, true);
    this.matchInlineFunctionWithArgsRegex = this.inlineFunctionRegexFactory(names, false, false);
  }

  setClientFunctions(
    clientFunctions: Record<string, FunctionDefinition>,
    handler?: HandleFunctionMethod,
    docsUrlResolver?: (funcName: string) => string,
  ) {
    this.clientFunctions = parseDefinitions(clientFunctions, true);
    this.clientDocsUrlResolver = docsUrlResolver;
    for (const name in clientFunctions) {
      this.allFunctionsNames.add(name);
      if (clientFunctions[name].aliases) {
        clientFunctions[name].aliases?.forEach(alias => this.allFunctionsNames.add(alias));
      }
    }
    const names = Array.from(this.allFunctionsNames);
    this.objectFunctionRegex = this.objectFunctionRegexFactory(names);
    this.inlineFunctionRegex = this.inlineFunctionRegexFactory(names, true, true);
    this.matchInlineFunctionWithArgsRegex = this.inlineFunctionRegexFactory(names, false, false);
    this.handleClientFunction = handler;
  }

  get(name: string, args?: Record<string, any>) {
    return this.clientFunctions?.[name] ?? functions[name];
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
    let func = functionsParser.get(funcName);
    if (!func) return null;
    if (func.subfunctions || callback) {
      const argsWithoutParenthesis = m[3];
      const args = parseArgs(func, argsWithoutParenthesis);
      func = getSubfunction(func, args);
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
        const func = getSubfunction(this.get(funcName), data);
        return {
          name: funcName,
          func,
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

export const functionsParser = new FunctionsParser();

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

export const getSubfunction = (func: FunctionDescriptor, args?: Record<string, any>) => {
  if (!func.subfunctions || !args) return func;
  for (const subFunc of func.subfunctions) {
    if (
      subFunc.if.every(
        c => (args[c.argument] ?? subFunc.then.defaultValues?.[c.argument])?.toString().toUpperCase() === c.equals,
      )
    ) {
      return subFunc.then; // replace func instance based on args
    }
  }
  return func;
};
