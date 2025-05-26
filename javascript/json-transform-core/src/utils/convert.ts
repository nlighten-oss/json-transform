import { functionsParser, parseArgs } from "../functions/functionsParser";
import { Argument } from "../functions/types";

function parseByType(arg: Argument, value: any) {
  if (typeof value === "string" && (value.startsWith("$") || value.startsWith("#"))) {
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

export const convertFunctionsToObjects = (definition: any): any => {
  if (!definition) {
    return definition;
  }
  if (typeof definition === "string") {
    let result: any = definition;
    functionsParser.matchInline(definition, (funcName, func, value, args) => {
      const funcArgs = func.arguments?.slice().sort((a, b) => (a?.position ?? Infinity) - (b?.position ?? Infinity));
      result = {
        ["$$" + funcName]: convertFunctionsToObjects(value) ?? true,
      };
      if (funcArgs) {
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

//const test = "$$map(##current.name):$$flat:$.names";
//console.log(JSON.stringify(convertFunctionsToObjects(test), null, 2));

const escapeString = (input: string) => {
  return `'${input.replace(/\\/g, "\\\\").replace(/'/g, "\\'")}'`;
};

export const tryConvertFunctionsToInline = (definition: any) => {
  const type = typeof definition;
  if (!definition || type !== "object") {
    return definition;
  }
  // must be an object
  const match = functionsParser.matchObject(definition);
  if (
    !match ||
    // arrays and non-function objects can't be converted to string values
    Array.isArray(match.value) ||
    (match.value && typeof match.value === "object" && !functionsParser.matchObject(match.value))
  ) {
    return definition;
  }
  // match.value is either primitive or an object function
  let result = `$$${match.name}`;
  const spec = structuredClone(match.spec);
  const argList = Object.keys(spec).filter(key => key !== result);
  let possible = true;
  if (argList.length && match.func.arguments?.length) {
    result += "(";
    const outputValues: string[] = [];
    const funcArgs = match.func.arguments.slice().sort((a, b) => (a?.position ?? Infinity) - (b?.position ?? Infinity));
    for (const arg of funcArgs) {
      let argValue = tryConvertFunctionsToInline(spec[arg.name]);
      if (argValue !== null && typeof argValue !== "undefined") {
        spec[arg.name] = argValue; // store it back for output
      }
      if (argValue && (Array.isArray(argValue) || typeof argValue === "object")) {
        possible = false;
      } else if (possible) {
        if (
          typeof argValue === "string" &&
          (argValue.startsWith("$") || argValue.startsWith("#") || argValue.includes(","))
        ) {
          argValue = escapeString(argValue);
        }
        outputValues.push(argValue);
      }
    }
    if (possible) {
      // remove trailing nulls
      for (let i = outputValues.length - 1; i >= 0; i--) {
        if (outputValues[i] !== null && typeof outputValues[i] !== "undefined") {
          break;
        }
        outputValues.pop();
      }
      result += outputValues.map(x => x ?? "") + ")";
    }
  }
  if (match.value) {
    const argValue = tryConvertFunctionsToInline(match.value);
    spec["$$" + match.name] = argValue; // store it back for output
    result += ":";
    if (argValue && (Array.isArray(argValue) || typeof argValue === "object")) {
      return spec;
    } else if (possible) {
      result += argValue;
    }
  }
  return possible ? result : spec;
};

// const test2 = {
//   $$map: {
//     $$flat: "$.names",
//   },
//   to: "##current.name",
// };
// console.log(JSON.stringify(tryConvertFunctionsToInline(test2), null, 2));
