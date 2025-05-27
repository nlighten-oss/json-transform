import { Argument, FunctionDefinition, FunctionDescriptor } from "./types";
import parseDefinitions from "./parseDefinitions";
import { HandleFunctionMethod } from "../ParseContext";
import functions from "./functions";
import { TokenizedInlineFunction, tokenizeInlineFunction } from "./tokenizeInlineFunction";

type ObjectFunctionMatchResult = {
  name: string;
  func: FunctionDescriptor;
  value: any;
  args: Record<string, any>;
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
  private inlineFunctionRegexFactory = (names: string[]) =>
    new RegExp(`\\$\\$(${names.join("|")})(\\((.*?)\\))?(:([^"]*)|$|")`, "g");

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
    this.inlineFunctionRegex = this.inlineFunctionRegexFactory(names);
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
    this.inlineFunctionRegex = this.inlineFunctionRegexFactory(names);
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
    callback?: (funcName: string, func: FunctionDescriptor, value: any, args: Record<string, any>) => any,
  ) {
    if (typeof data !== "string") return null;

    const m = tokenizeInlineFunction(data);
    if (!m) {
      return null;
    }
    let funcName = m.name;
    let func = functionsParser.get(funcName);
    if (!func) return null;
    if (func.aliasTo) funcName = func.aliasTo;
    if (func.subfunctions || callback) {
      const args =
        m.args?.reduce(
          (a, c, i) => {
            func.arguments?.forEach(fa => {
              if (fa.position === i && fa.name) {
                a[fa.name] = c.value;
              }
            });
            return a;
          },
          {} as Record<string, any>,
        ) ?? {};
      func = getSubfunction(func, args);
      if (callback) {
        const inlineFunctionValue = m.input?.value;
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
        const args = { ...data };
        delete args[key]; // remove the function key from args
        if (func.argumentsAsInputSchema && Object.keys(args).length === 0 && Array.isArray(value)) {
          value.forEach((argVal, position) => {
            const arg = func.arguments?.find(a => a.position === position);
            if (arg) {
              args[arg.name] = argVal;
            }
          });
        }

        return {
          name: func.aliasTo ?? funcName,
          func,
          value,
          args,
        };
      }
    }
  }

  matchAllObjectFunctionsInLine(line: string) {
    return line.matchAll(this.objectFunctionRegex);
  }
  matchAllInlineFunctionsInLine(line: string) {
    const matches: ({ index: number } & TokenizedInlineFunction)[] = [];
    let match: TokenizedInlineFunction | undefined;
    let indexOffset = line.indexOf("$$");
    let str = line.substring(indexOffset).trimEnd();
    if (line[indexOffset - 1] === '"' && str.at(-1) === '"') {
      str = str.slice(0, -1).replace(/\\'/g, "\\\\'");
      // try {
      //   str = JSON.parse('"' + str); //.replace(/\\/g, "\\\\"); // duplicate all backslashes to get the right lengths
      // } catch (e: any) {}
    }
    while ((match = tokenizeInlineFunction(str))) {
      (match as any).index = indexOffset; // add index to match
      match.args?.forEach(arg => {
        arg.index += indexOffset;
        const delta = arg.value?.match(/'/g)?.length ?? 0;
        arg.length -= delta;
        indexOffset -= delta;
      });
      if (match.input) {
        match.input.index += indexOffset;
        const delta = match.input.value?.match(/'/g)?.length ?? 0;
        match.input.length -= delta;
        indexOffset -= delta;
      }
      matches.push(match as any);
      if (!match.input || !match.input.value?.startsWith("$$")) {
        break;
      }
      indexOffset = match.input.index;
      str = line.substring(match.input.index);
    }
    console.debug("debug", matches);
    return matches;
  }
}

export const functionsParser = new FunctionsParser();

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
