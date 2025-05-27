import { functionsParser } from "../functions/functionsParser";
import { Argument } from "../functions/types";

const isMap = (value: any): value is Record<string, any> =>
  value && typeof value === "object" && !Array.isArray(value) && value._isBigNumber !== true;

function parseByType(arg: Argument, value: any) {
  if (
    functionsParser.matchObject(value) ||
    (typeof value === "string" && (value.startsWith("$") || value.startsWith("#")))
  ) {
    // if the value is a path/function, return it as is
    return value;
  }
  switch (arg.type) {
    case "const":
      return arg.const;
    case "boolean":
      return value === "true" || value === true;
    case "integer":
    case "long":
    case "BigDecimal":
      return Number(value);
    default:
      return value;
  }
}

const removeTrailingNulls = (arr: any[]) => {
  for (let i = arr.length - 1; i >= 0; i--) {
    if (arr[i] !== null && typeof arr[i] !== "undefined") {
      break;
    }
    arr.pop();
  }
  return arr;
};

export const convertFunctionsToObjects = (definition: any): any => {
  if (!definition) {
    return definition;
  }
  if (typeof definition === "string") {
    let result: any = definition;
    functionsParser.matchInline(definition, (funcName, func, value, args) => {
      const funcArgs = func.arguments
        ?.slice()
        .filter(a => typeof a.position !== "undefined")
        .sort((a, b) => (a?.position ?? Infinity) - (b?.position ?? Infinity));
      result = {
        ["$$" + funcName]: func.argumentsAsInputSchema
          ? removeTrailingNulls(funcArgs?.map(a => parseByType(a, convertFunctionsToObjects(args[a.name]))) ?? [])
          : convertFunctionsToObjects(value) ?? true,
      };
      if (funcArgs && !func.argumentsAsInputSchema) {
        for (const arg of funcArgs) {
          const argValue = args?.[arg.name];
          result[arg.name] = parseByType(arg, convertFunctionsToObjects(argValue));
        }
      }
    });
    return result;
  }
  if (Array.isArray(definition)) {
    return definition.map(convertFunctionsToObjects);
  }
  if (typeof definition === "object") {
    const convertedEntries = Object.entries(definition).map(e => [e[0], convertFunctionsToObjects(e[1])]);
    return Object.fromEntries(convertedEntries);
  }
  return definition;
};

const escapeString = (input: string) => {
  return `'${input.replace(/\\/g, "\\\\").replace(/'/g, "\\'")}'`;
};

export const tryConvertFunctionsToInline = (definition: any): any => {
  const type = typeof definition;
  if (!definition || type !== "object") {
    return definition;
  }
  // must be an object / array
  const match = functionsParser.matchObject(definition);

  if (!match && (Array.isArray(definition) || isMap(definition))) {
    if (Array.isArray(definition)) {
      return definition.map(tryConvertFunctionsToInline);
    } else if (isMap(definition)) {
      // if it's a map, we need to convert it to an object
      const result: Record<string, any> = {};
      for (const [key, value] of Object.entries(definition)) {
        result[key] = tryConvertFunctionsToInline(value);
      }
      return result;
    }
  }
  if (
    !match ||
    // arrays and non-function objects can't be converted to string values
    (!match.func.argumentsAsInputSchema && Array.isArray(match.value)) ||
    (isMap(match.value) && !functionsParser.matchObject(match.value))
  ) {
    return definition;
  }
  // match.value is either primitive or an object function
  const funcKey = `$$${match.name}`;
  let result = funcKey;
  const spec = structuredClone(definition); // clone the object to avoid modifying the original
  const argList = Object.keys(spec).filter(key => key !== funcKey);
  let possible = true;
  const argValues: string[] = [];
  if (argList.length && match.func.arguments?.length) {
    const funcArgs = match.func.arguments.slice().sort((a, b) => (a?.position ?? Infinity) - (b?.position ?? Infinity));
    for (const arg of funcArgs) {
      let argValue = tryConvertFunctionsToInline(spec[arg.name]);
      if (argValue !== null && typeof argValue !== "undefined") {
        spec[arg.name] = argValue; // store it back for output
      }
      if (
        argValue &&
        (Array.isArray(argValue) ||
          typeof argValue === "object" ||
          (typeof argValue === "string" && argValue.startsWith("$$") && argValue.includes(":")))
      ) {
        possible = false;
      } else if (possible) {
        if (
          typeof argValue === "string" &&
          (argValue.startsWith("$") || argValue.startsWith("#") || argValue.includes(","))
        ) {
          argValue = escapeString(argValue);
        }
        argValues.push(argValue);
      }
    }
  } else if (Array.isArray(match.value) && match.func.argumentsAsInputSchema) {
    for (let i = 0; i < match.value.length; i++) {
      let argValue = tryConvertFunctionsToInline(match.value[i]);
      const argName = match.func.arguments?.find(x => x.position === i)?.name;
      if (argName && argValue !== null && typeof argValue !== "undefined") {
        spec[funcKey][i] = argValue; // store it back for output
      }
      if (
        argValue &&
        (Array.isArray(argValue) ||
          typeof argValue === "object" ||
          (typeof argValue === "string" && argValue.startsWith("$$") && argValue.includes(":")))
      ) {
        possible = false;
      } else if (possible) {
        if (
          typeof argValue === "string" &&
          (argValue.startsWith("$") || argValue.startsWith("#") || argValue.includes(","))
        ) {
          argValue = escapeString(argValue);
        }
        argValues.push(argValue);
      }
    }
  }
  if (possible && argValues?.length) {
    removeTrailingNulls(argValues);
    if (argValues.length > 0) {
      result += "(" + argValues.map(x => x ?? "") + ")";
    }
  }
  if (match.value && !match.func.argumentsAsInputSchema) {
    const argValue = tryConvertFunctionsToInline(match.value);
    spec["$$" + match.name] = argValue; // store it back for output
    result += ":";
    if (argValue && (Array.isArray(argValue) || typeof argValue === "object")) {
      possible = false; // the primary argument is not primitive
    } else if (possible) {
      result += argValue;
    }
  }
  return possible ? result : spec;
};
