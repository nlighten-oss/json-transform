import {
  areSimilar,
  cleanParsedSchemaProperty,
  inferJSONSchemaType,
  type TypeSchema,
} from "@nlighten/json-schema-utils";
import { EmbeddedTransformerFunction } from "./functions/types";
import { functionsParser } from "./functions/functionsParser";
import { matchJsonPathFunction } from "./jsonpath/jsonpathFunctions";
import { jsonpathJoin, VALID_ID_REGEXP } from "./jsonpath/jsonpathJoin";
import ParseContext, { HandleFunctionMethod, ParseMethod } from "./ParseContext";

const ALL_DIGITS = /^\d+$/;

class TransformerParser {
  static copySubPathsOnWalk(
    sourcePath: string,
    targetPath: string,
    localPath: string,
    previousPaths: string[],
    paths: string[],
    context: ParseContext,
  ) {
    let simplePath = sourcePath.replace(/\[[^\]]+]/g, "[]");
    const sourceIsArray = sourcePath.includes("[*]") || sourcePath.includes("[?(");
    if (context.paths && sourceIsArray) {
      const itemType = context.resolve(sourcePath) ?? context.resolve(simplePath);
      context.paths[targetPath + localPath] = {
        type: "array",
        items: itemType
          ? structuredClone(itemType)
          : context.resolve(simplePath.slice(0, -2))
            ? structuredClone(context.resolve(simplePath.slice(0, -2)))
            : undefined,
      };
      if (!itemType) {
        simplePath = simplePath.slice(0, -2);
      }
    }
    const baseSubPath = localPath + (sourceIsArray ? "[]" : "");
    previousPaths
      .filter(
        x =>
          x === sourcePath ||
          x.startsWith(sourcePath + ".") ||
          x.startsWith(sourcePath + "[") ||
          x === simplePath ||
          x.startsWith(simplePath + ".") ||
          x.startsWith(simplePath + "["),
      )
      .forEach(prevPath => {
        const detectedBySimple =
          prevPath === simplePath || prevPath.startsWith(simplePath + ".") || prevPath.startsWith(simplePath + "[");
        const subPath = baseSubPath + prevPath.substring(detectedBySimple ? simplePath.length : sourcePath.length);
        // copy types
        if (context.paths && context.resolve(prevPath)) {
          context.paths[targetPath + subPath] = structuredClone(context.resolve(prevPath)) as TypeSchema;
        }
        if (!paths.includes(targetPath + subPath)) {
          paths.push(targetPath + subPath);
        }
      });
  }

  static isCustomJsonPath(data: any, context: ParseContext) {
    return typeof data === "string" && (data.startsWith("$.") || data.startsWith("#") || data === "$");
  }

  static rawWalkOnObject(targetPath: string, data: any, s: string, paths: string[], context: ParseContext) {
    if (s) paths.push(targetPath + s);
    if (context.paths && typeof data !== "undefined") {
      context.paths[targetPath + s] = inferJSONSchemaType(data);
    }

    // definition is Array
    if (Array.isArray(data)) {
      for (let i = 0; i < data.length; i++) {
        TransformerParser.rawWalkOnObject(targetPath, data[i], s + "[" + i + "]", paths, context);
      }
      const arrayType = context.paths?.[targetPath + s];
      if (arrayType && data.length > 0) {
        arrayType.items = data.map((c, i) => structuredClone(context.resolve(targetPath + s + `[${i}]`) as TypeSchema));
      }
      return;
    }

    if (data && typeof data === "object") {
      // definition is plain object (not OJF)
      for (const p in data) {
        if (VALID_ID_REGEXP.test(p)) {
          TransformerParser.rawWalkOnObject(targetPath, data[p], s + "." + p, paths, context);
        } else {
          TransformerParser.rawWalkOnObject(
            targetPath,
            data[p],
            s + "[" + (ALL_DIGITS.test(p) ? p : JSON.stringify(p)) + "]",
            paths,
            context,
          );
        }
      }
    }
  }

  /**
   * Traverse an object and set all paths encountered to targetPath + localPath
   * @param definition
   * @param targetPath extract output paths to this prefix
   * @param localPath current path prefix in traversal
   * @param previousPaths paths that are input to this transformation (but might not be part of the output)
   * @param paths result paths encountered to be in the output object of this transformation
   * @param context map of path to schema, should contain already existing paths to extract from (will be updated if exists)
   */
  public static parse: ParseMethod = (definition, targetPath, localPath, previousPaths, paths, context) => {
    if (localPath.includes("*") || localPath.includes("..")) return;
    if (localPath) paths.push(targetPath + localPath);

    // definition is a reference inside $$map / $$transform functions
    if (definition === "##index") {
      if (context.paths) {
        context.paths[targetPath + localPath] = { type: "integer" };
      }
      return;
    }

    // definition is string and custom JsonPath (e.g. $ / $. / # / ##)
    if (context.isJsonPathReference(definition)) {
      // check if jsonpath is using a function (e.g. $.concat())
      const jsonPathFunctionSchema = matchJsonPathFunction(definition);
      if (jsonPathFunctionSchema) {
        if (context.paths) {
          context.paths[targetPath + localPath] = structuredClone(jsonPathFunctionSchema);
          if (jsonPathFunctionSchema.type === "array") {
            paths.push(targetPath + localPath + "[]");
            context.paths[targetPath + localPath + "[]"] = structuredClone(jsonPathFunctionSchema.items) as any;
          }
        }
      } else {
        // copy all the sub paths of that path object
        TransformerParser.copySubPathsOnWalk(definition, targetPath, localPath, previousPaths, paths, context);
        if (context.paths && !context.resolve(targetPath + localPath)) {
          context.paths[targetPath + localPath] = context.resolve(definition)
            ? structuredClone(context.resolve(definition))
            : ({} as any);
        }
      }
      return;
    }

    if (
      functionsParser.matchInline(definition, (funcName, func, value, args) => {
        TransformerParser.handleFunction(
          "inline",
          funcName,
          func,
          value,
          args,
          targetPath,
          localPath,
          previousPaths,
          paths,
          context,
        );
      })
    ) {
      // inline function - handled
      return;
    }

    if (definition && typeof definition === "object" && !Array.isArray(definition)) {
      const objFunc = functionsParser.matchObject(definition, true);
      if (objFunc) {
        TransformerParser.handleFunction(
          "object",
          objFunc.name,
          objFunc.func,
          objFunc.value,
          objFunc.spec,
          targetPath,
          localPath,
          previousPaths,
          paths,
          context,
        );
      } else {
        // definition is plain object
        for (const p in definition) {
          if (p === "*") {
            if (context.isJsonPathReference(definition["*"])) {
              TransformerParser.copySubPathsOnWalk(
                definition["*"],
                targetPath,
                localPath,
                previousPaths,
                paths,
                context,
              );
            } else if (Array.isArray(definition["*"])) {
              definition["*"].forEach(
                pc =>
                  context.isJsonPathReference(pc) &&
                  TransformerParser.copySubPathsOnWalk(pc, targetPath, localPath, previousPaths, paths, context),
              );
            }
            continue;
          }
          const propPath = VALID_ID_REGEXP.test(p) ? "." + p : "[" + (ALL_DIGITS.test(p) ? p : JSON.stringify(p)) + "]";
          TransformerParser.parse(definition[p], targetPath, localPath + propPath, previousPaths, paths, context);
          if (context.paths) {
            if (!context.paths[targetPath + localPath]) {
              context.paths[targetPath + localPath] = { type: "object", additionalProperties: false };
            }
            const properties = context.paths[targetPath + localPath].properties ?? {};
            if (context.paths[targetPath + localPath + propPath]) {
              properties[p] = structuredClone(context.resolve(targetPath + localPath + propPath)) as TypeSchema;
              context.paths[targetPath + localPath].properties = properties;
            }
          }
        }
      }
    } else if (Array.isArray(definition) && definition.length > 0) {
      if (context.paths) {
        context.paths[targetPath + localPath] = { type: "array" };
      }
      for (let i = 0; i < definition.length; i++) {
        TransformerParser.parse(definition[i], targetPath, localPath + "[" + i + "]", previousPaths, paths, context);
      }
      if (context.paths) {
        // check if all paths are the same, then compact it
        const firstType = context.paths[targetPath + localPath + "[0]"];
        const singleType =
          firstType &&
          definition.every((x: any, i: number) =>
            areSimilar(firstType, context.resolve(targetPath + localPath + "[" + i + "]")),
          );
        if (singleType) {
          const pref = targetPath + localPath;
          const firstIndex = paths.indexOf(targetPath + localPath + "[0]");
          const lastIndex = paths.indexOf(targetPath + localPath + "[" + (definition.length - 1) + "]");
          for (let i = firstIndex; i <= lastIndex; i++) {
            const x = paths[i];
            if (context.resolve(x)) {
              context.paths[pref + x.slice(pref.length).replace(/^\[\d+]/, "[]")] = structuredClone(
                context.resolve(x),
              ) as TypeSchema;
            }
          }
          Array.prototype.splice.apply(
            paths,
            ([firstIndex, paths.length - firstIndex] as any[]).concat(
              paths.slice(lastIndex).map(x => pref + x.slice(pref.length).replace(/^\[\d+]/, "[]")),
            ) as [start: number, deleteCount: number, ...items: any[]],
          );
          context.paths[targetPath + localPath].items = structuredClone(context.resolve(targetPath + localPath + "[]"));
        } else {
          context.paths[targetPath + localPath].items = definition.map(
            (c, i) => structuredClone(context.resolve(targetPath + localPath + `[${i}]`)) as TypeSchema,
          );
        }
      }
    } else {
      // definition is either unrecognized string (treated as string) or another json type
      if (context.paths && typeof definition !== "undefined") {
        context.paths[targetPath + localPath] = inferJSONSchemaType(definition);
      }
    }
  };

  static findNonNull(value: string, context: ParseContext) {
    if (!context.paths) return null;
    const items = context.resolve(value)?.items;
    if (!Array.isArray(items)) return null;
    return items.findIndex(x => x.type !== "null");
  }

  /**
   * value is array of unknown size.
   * if value is array, take first (non null preferably),
   * otherwise, try to form a jsonpath to first non-null element
   */
  static copyArrayItemTypeOnWalk(
    value: any[] | string,
    targetPath: string,
    localPath: string,
    previousPaths: string[],
    paths: string[],
    context: ParseContext,
  ) {
    const dataWithResultType = Array.isArray(value)
      ? (context.paths && value.find((x: any) => context.resolve(x)?.type !== "null")) ??
        value.find(x => typeof x !== "undefined" && x !== null) ??
        value[0]
      : previousPaths.includes(value + "[0]")
        ? value + `[${TransformerParser.findNonNull(value, context) ?? 0}]`
        : value + "[]";
    TransformerParser.parse(dataWithResultType, targetPath, localPath, previousPaths, paths, context);
  }

  /**
   * Handle resolving of function output type
   */
  static handleFunction: HandleFunctionMethod = (
    detectedAs,
    funcName,
    func,
    value,
    args,
    targetPath,
    localPath,
    previousPaths,
    paths,
    context,
  ) => {
    let unhandled = false;
    switch (funcName) {
      case EmbeddedTransformerFunction.at:
      case EmbeddedTransformerFunction.coalesce:
      case EmbeddedTransformerFunction.concat:
      case EmbeddedTransformerFunction.find:
      case EmbeddedTransformerFunction.first:
      case EmbeddedTransformerFunction.flat: {
        TransformerParser.copyArrayItemTypeOnWalk(value, targetPath, localPath, previousPaths, paths, context);
        if (funcName === EmbeddedTransformerFunction.concat || funcName === EmbeddedTransformerFunction.flat) {
          // fix type if not array
          if (context.paths && context.resolve(targetPath + localPath)?.type !== "array") {
            context.paths[targetPath + localPath] = {
              type: "array",
              items: context.resolve(targetPath + localPath),
            };
          }
        }
        break;
      }
      case EmbeddedTransformerFunction.distinct:
      case EmbeddedTransformerFunction.filter:
      case EmbeddedTransformerFunction.reverse:
      case EmbeddedTransformerFunction.slice:
      case EmbeddedTransformerFunction.sort: {
        // input is unknown size array (result might be smaller in size, but of same type)
        TransformerParser.copyArrayItemTypeOnWalk(value, targetPath, localPath + "[]", previousPaths, paths, context);
        if (!paths.includes(targetPath + localPath + "[]")) {
          paths.push(targetPath + localPath + "[]");
          if (context.paths) {
            if (!context.resolve(targetPath + localPath + "[]")) {
              context.paths[targetPath + localPath + "[]"] = structuredClone(
                context.resolve(targetPath + localPath + "[0]") ?? {
                  type: "object",
                },
              );
            }
          }
        }
        if (
          context.paths &&
          (!context.resolve(targetPath + localPath) || context.resolve(targetPath + localPath)?.type !== "array")
        ) {
          context.paths[targetPath + localPath] = {
            type: "array",
            items: structuredClone(context.resolve(targetPath + localPath + "[]")),
          };
        }
        break;
      }
      case EmbeddedTransformerFunction.if: {
        if (typeof args.then !== "undefined") {
          TransformerParser.parse(args.then, targetPath, localPath, previousPaths, paths, context);
          break;
        }
        // input is array of known size (object with result type is at index 1)
        const dataWithResultType = typeof value === "string" ? value + "[1]" : value?.[1];
        TransformerParser.parse(dataWithResultType, targetPath, localPath, previousPaths, paths, context);
        break;
      }
      case EmbeddedTransformerFunction.reduce: {
        if (typeof args.identity !== "undefined") {
          TransformerParser.parse(args.identity, targetPath, localPath, previousPaths, paths, context);
        }
        break;
      }
      case EmbeddedTransformerFunction.value: {
        // input and output should have the same schema
        TransformerParser.parse(value, targetPath, localPath, previousPaths, paths, context);
        break;
      }
      case EmbeddedTransformerFunction.raw: {
        // input type is the result type
        TransformerParser.rawWalkOnObject(targetPath, value, localPath, paths, context);
        break;
      }
      case EmbeddedTransformerFunction.max:
      case EmbeddedTransformerFunction.min: {
        // if (localPath) paths.push(targetPath + localPath);
        const typ = args?.type?.toUpperCase();
        switch (typ) {
          case "NUMBER":
            if (context.paths) {
              context.paths[targetPath + localPath] = { type: "number" };
            }
            break;
          case "BOOLEAN":
            if (context.paths) {
              context.paths[targetPath + localPath] = { type: "boolean" };
            }
            break;
          case "STRING":
            if (context.paths) {
              context.paths[targetPath + localPath] = { type: "string" };
            }
            break;
          default:
            TransformerParser.copyArrayItemTypeOnWalk(value, targetPath, localPath, previousPaths, paths, context);
            return true;
        }
        break;
      }
      case EmbeddedTransformerFunction.partition: {
        paths.push(targetPath + localPath + "[]");
        TransformerParser.copyArrayItemTypeOnWalk(value, targetPath, localPath + "[][]", previousPaths, paths, context);
        if (context.paths) {
          if (!context.resolve(targetPath + localPath + "[]")) {
            context.paths[targetPath + localPath + "[]"] = {
              type: "array",
              items: structuredClone(context.resolve(targetPath + localPath + "[][]")),
            };
          }
          context.paths[targetPath + localPath] = {
            type: "array",
            items: structuredClone(context.resolve(targetPath + localPath + "[]")),
          };
        }
        break;
      }
      case EmbeddedTransformerFunction.lookup: {
        // input is array of known size (object with result type is at index 0)
        // * This is a special case because the transformer uses ##current/{alias} / ##index to reference the object / iteration element
        const itemsData = value;
        const dataWithCurrentType = Array.isArray(itemsData)
          ? itemsData[0]
          : previousPaths.includes(itemsData + "[0]")
            ? itemsData + "[0]" // this is not the best approach (other indices may have different fields)
            : itemsData + "[]";

        const currentPaths: string[] = [];
        TransformerParser.parse(dataWithCurrentType, "##current", "", previousPaths, currentPaths, context);

        if (Array.isArray(args.using)) {
          if (args.to) {
            args.using.forEach((use: any) => {
              if (typeof use.as !== "string") return; // invalid
              const dataWithMatchType = Array.isArray(use.with)
                ? use.with[0]
                : previousPaths.includes(use.with + "[0]")
                  ? use.with + "[0]" // this is not the best approach (other indices may have different fields)
                  : use.with + "[]";
              TransformerParser.parse(dataWithMatchType, "##" + use.as, "", previousPaths, currentPaths, context);
            });

            const prevPaths = currentPaths.concat(previousPaths.filter(x => !x.startsWith("##current")));
            TransformerParser.parse(args.to, targetPath, localPath + "[]", prevPaths, paths, context);
          } else {
            args.using.forEach((use: any) => {
              if (typeof use.as !== "string") return; // invalid
              const dataWithMatchType = Array.isArray(use.with)
                ? use.with[0]
                : previousPaths.includes(use.with + "[0]")
                  ? use.with + "[0]" // this is not the best approach (other indices may have different fields)
                  : use.with + "[]";
              TransformerParser.parse(dataWithMatchType, targetPath, localPath + "[]", previousPaths, paths, context);
            });
            const dataWithResultType = typeof value === "string" ? value + "[0]" : value?.[0];
            TransformerParser.parse(dataWithResultType, targetPath, localPath + "[]", previousPaths, paths, context);
          }
        }

        if (context.paths) {
          context.paths[targetPath + localPath] = {
            type: "array",
            items: structuredClone(context.resolve(targetPath + localPath + "[]")),
          };
          // remove all ##current and other aliases from paths
          for (const p of currentPaths) {
            delete context.paths[p];
          }
        }
        break;
      }
      case EmbeddedTransformerFunction.map:
      case EmbeddedTransformerFunction.transform: {
        // input is array of known size
        // * This is a special case because the transformer uses ##current / ##index to reference the object / iteration element
        const dataWithResultType = args.to ?? (typeof value === "string" ? value + "[1]" : value?.[1]);
        const itemsData = args.to ? value : typeof value === "string" ? value + "[0]" : value?.[0];
        let targetSuffix = "";
        const currentPaths: string[] = [];
        if (funcName === EmbeddedTransformerFunction.map) {
          // at any existing index of array at index 0
          const dataWithCurrentType = Array.isArray(itemsData)
            ? itemsData[0]
            : previousPaths.includes(itemsData + "[0]")
              ? itemsData + "[0]" // this is not the best approach (other indices may have different fields)
              : itemsData + "[]";
          TransformerParser.parse(dataWithCurrentType, "##current", "", previousPaths, currentPaths, context);
          targetSuffix = "[]";
        } else if (funcName === EmbeddedTransformerFunction.transform) {
          // at index 0
          TransformerParser.parse(itemsData, "##current", "", previousPaths, currentPaths, context);
        }

        const prevPaths = currentPaths.concat(previousPaths.filter(x => !x.startsWith("##current")));
        TransformerParser.parse(dataWithResultType, targetPath, localPath + targetSuffix, prevPaths, paths, context);

        if (funcName === EmbeddedTransformerFunction.map) {
          if (context.paths) {
            context.paths[targetPath + localPath] = {
              type: "array",
              items: structuredClone(context.resolve(targetPath + localPath + "[]")),
            };
          }
        }
        if (context.paths) {
          // remove all ##current from paths
          for (const p of currentPaths) {
            delete context.paths[p];
          }
        }
        break;
      }
      case EmbeddedTransformerFunction.switch: {
        if (typeof args.default !== "undefined") {
          TransformerParser.parse(args.default, targetPath, localPath, previousPaths, paths, context);
          break;
        } else {
          // try to get type from default, otherwise from the first case
          if (args.cases && typeof args.cases === "object") {
            const dataWithResultType = args.cases[Object.keys(args.cases)[0]];
            if (dataWithResultType) {
              TransformerParser.parse(dataWithResultType, targetPath, localPath, previousPaths, paths, context);
              break;
            }
          }
          // can't detect type, put object
          if (context.paths) {
            context.paths[targetPath + localPath] = { type: "object" };
          }
        }
        break;
      }
      default: {
        if (
          functionsParser.handleClientFunction?.call(
            null,
            detectedAs,
            funcName,
            func,
            value,
            args,
            targetPath,
            localPath,
            previousPaths,
            paths,
            context,
            TransformerParser.parse,
          )
        ) {
          break;
        }

        unhandled = true;
      }
    }
    if (unhandled && func.parsedOutputSchema) {
      if (func.outputSchema && context.paths) {
        context.paths[targetPath + localPath] = structuredClone(func.outputSchema);
      }
      func.parsedOutputSchema.paths.forEach(p => {
        const key = jsonpathJoin(targetPath + localPath, p.$path);
        paths.push(key);
        if (context.paths) {
          context.paths[key] = cleanParsedSchemaProperty(p);
        }
      });
    }
    return !unhandled;
  };
}

/**
 * Traverse an object and set all paths encountered to targetPath + localPath
 * @param definition
 * @param targetPath extract output paths to this prefix
 * @param previousPaths paths that are input to this transformation (but might not be part of the output)
 * @param paths result paths encountered to be in the output object of this transformation
 * @param typesMap map of path to schema, should contain already existing paths to extract from (will be updated if exists)
 * @param additionalContext additional context to resolve types from
 */
export function parseTransformer(
  definition: any,
  targetPath: string,
  previousPaths: string[],
  paths: string[] = [],
  typesMap?: Record<string, TypeSchema>,
  additionalContext?: Record<string, TypeSchema>,
) {
  const context = new ParseContext(typesMap, additionalContext, previousPaths);
  return TransformerParser.parse(definition, targetPath, "", previousPaths, paths, context);
}
