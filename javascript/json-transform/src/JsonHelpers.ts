import { v4 as uuidv4 } from "uuid";
import DocumentContext from "./DocumentContext";
import { BigDecimal, JSONBig } from "./functions/common/FunctionHelpers";
import { areSimilar } from "@nlighten/json-schema-utils";
import { Comparator, ComparatorFactory } from "@wortise/sequency";
import BigNumber from "bignumber.js";

const JSONPATH_ROOT = "$",
  JSONPATH_ROOT_ESC = "\\$",
  JSONPATH_ROOT_CHAR = JSONPATH_ROOT.charAt(0),
  JSONPATH_ALT_PREFIX = "#",
  JSONPATH_ALT_PREFIX_ESC = "\\#";

const isNullOrUndefined = (value: any): value is null | undefined => value == null || typeof value === "undefined";
const isNullOrEmpty = (value?: string | null): value is null | undefined =>
  value == null || typeof value === "undefined" || value === "";
const isMap = (value: any): value is Record<string, any> =>
  value && typeof value === "object" && !Array.isArray(value) && !BigNumber.isBigNumber(value);

function getAsString<T extends string | number | boolean | object>(value: T): string;
function getAsString<T extends null | undefined>(value: T): null;
function getAsString(value: any): string | null {
  if (isNullOrUndefined(value)) {
    return null;
  }
  if (typeof value === "string") {
    return value;
  }
  if (
    typeof value === "number" ||
    typeof value === "bigint" ||
    value instanceof BigDecimal ||
    value instanceof BigNumber
  ) {
    return value.toString();
  }
  if (typeof value === "boolean") {
    return value ? "true" : "false";
  }
  return JSONBig.stringify(value);
}

const numberCompare = (a: number, b: number) => {
  return a < b ? -1 : a === b ? 0 : 1;
};

const isNumberType = (a: any) => typeof a === "number" || typeof a === "bigint" || a instanceof BigDecimal;
const numberToBigDecimal = (a: any) => {
  return BigDecimal(typeof a === "bigint" ? a.toString() : a);
};

const compareTo = (a: any, b: any) => {
  if (Array.isArray(a) && Array.isArray(b)) {
    return numberCompare(a.length, b.length);
  } else if (isNumberType(a) && isNumberType(b)) {
    return numberToBigDecimal(a).comparedTo(numberToBigDecimal(b));
  } else if (a && b && typeof a === "object" && typeof b === "object") {
    return numberCompare(Object.keys(a).length, Object.keys(b).length);
  } else if (typeof a === "string" && typeof b === "string") {
    return a.localeCompare(b);
  } else if (typeof a === "boolean" && typeof b === "boolean") {
    return a === b ? 0 : a ? 1 : -1;
  } else if (isNullOrUndefined(a) && !isNullOrUndefined(b)) {
    return -1;
  } else if (!isNullOrUndefined(a) && isNullOrUndefined(b)) {
    return 1;
  }
  // incomparable
  return null;
};

const getDocumentContext = (value: any): DocumentContext => {
  return new DocumentContext(value);
};

const PRIMITIVE_TYPES_SET = new Set(["string", "number", "boolean", "bigint"]);
const IS_DIGIT_REGEX = /[0-9]/;

class DocumentContextSupplier {
  private value: any;
  constructor(value: any) {
    this.value = value;
  }
  get() {
    return getDocumentContext(this.value);
  }
}

const getRootFromPath = (path: string) => {
  const indexOfDot = path.indexOf(".");
  const indexOfIndexer = path.indexOf("[");
  const endOfKeyIndex =
    indexOfDot > -1 && indexOfIndexer > -1
      ? Math.min(indexOfDot, indexOfIndexer)
      : indexOfDot > -1
        ? indexOfDot
        : indexOfIndexer;
  return endOfKeyIndex < 0 ? path : path.substring(0, endOfKeyIndex);
};

const createPayloadResolver = (payload: any, additionalContext: Record<string, any>) => {
  const json = getDocumentContext(payload);
  const additionalJsons = !additionalContext
    ? {}
    : Object.entries(additionalContext).reduce(
        (a, [key, value]) => {
          if (PRIMITIVE_TYPES_SET.has(typeof value)) {
            a[key] = value;
          } else {
            a[key] = new DocumentContextSupplier(value);
          }
          return a;
        },
        {} as Record<string, any>,
      );

  return {
    get: (name: string) => {
      if (!name || name === "") return name;
      if (!name.startsWith(JSONPATH_ROOT) && !name.startsWith(JSONPATH_ALT_PREFIX)) {
        if (name.startsWith(JSONPATH_ROOT_ESC) || name.startsWith(JSONPATH_ALT_PREFIX_ESC)) {
          name = name.substring(1);
        }
        return name;
      }
      if (name.startsWith(JSONPATH_ALT_PREFIX) && name.length <= 5) {
        switch (name.toLowerCase()) {
          case "#uuid": {
            return uuidv4();
          }
          case "#null": {
            return null;
          }
          case "#now": {
            return new Date().toISOString();
          }
        }
      }
      if (
        name.length < 2 ||
        // fix for regex patterns being detected (e.g. $0)
        (name.charAt(1) != JSONPATH_ROOT_CHAR && !IS_DIGIT_REGEX.test(name.charAt(1)))
      ) {
        var nameKey = getRootFromPath(name);
        let res: any;
        if (Object.prototype.hasOwnProperty.call(additionalJsons, nameKey)) {
          var add = additionalJsons[nameKey];
          if (add instanceof DocumentContextSupplier) {
            // lazy evaluate document context until used for the first time
            add = add.get();
            additionalJsons[nameKey] = add;
          }
          if (add instanceof DocumentContext) {
            res = add.read(JSONPATH_ROOT + name.substring(nameKey.length));
          } else {
            res = add;
          }
        } else if (nameKey !== JSONPATH_ROOT) {
          return name; // unrecognized root
        } else {
          res = json.read(name);
        }
        return res;
      }
      return name;
    },
  };
};

const singleQuotedStringJsonParse = (input: string) => {
  try {
    return JSON.parse(`"${input.slice(1, -1).replace(/"/g, '\\"')}"`);
  } catch (e: any) {
    console.error(e);
    return null;
  }
};

const BIGINT_ZERO = BigInt(0);
const isTruthy = (value: any, javascriptStyle: boolean = true) => {
  if (typeof value === "boolean") {
    return value;
  } else if (typeof value === "number") {
    return value != 0;
  } else if (typeof value === "bigint") {
    return value !== BIGINT_ZERO;
  } else if (value instanceof BigDecimal) {
    return !value.isZero();
  } else if (typeof value === "string") {
    return javascriptStyle ? Boolean(value) : value.toLowerCase() === "true";
  } else if (Array.isArray(value)) {
    return value.length > 0;
  } else if (value && typeof value === "object") {
    return Object.keys(value).length > 0;
  }
  return !isNullOrUndefined(value);
};

const isEqual = (value: any, other: any): boolean => {
  if (value === other) {
    return true;
  }
  if (isNumberType(value) && isNumberType(other)) {
    return numberToBigDecimal(value).eq(numberToBigDecimal(other));
  }
  return areSimilar(value, other);
};

/**
 * Builds a missing paths parent elements. e.g for a path of a.b.c the result would be {@code {a:{b:{c:value}}}}
 *
 * @param value    the value to add at the end of the path
 * @param location the location to build
 * @return a wrapping element containing the value in it's path
 */
const wrapElement = (value: any, location: string[]) => {
  let point: string | undefined;
  let elm = value;

  while ((point = location.pop()) != null) {
    elm = { [point]: elm };
  }
  return elm;
};

/**
 * Builds a deque list of paths from a jsonPath string value (including map keys selectors)
 *
 * @param jsonPath the json path to build from
 * @return a dequeue with a list of element keys
 */
const extractPath = (jsonPath?: string | null) => {
  let paths: string[] = [];
  if (isNullOrUndefined(jsonPath) || jsonPath.trim() === "") {
    return paths;
  }
  let sb = "";
  const expecting: string[] = [];

  for (let i = 0; i < jsonPath.length; i++) {
    const c = jsonPath.charAt(i);
    if (c == "." && expecting.length == 0 && sb.length > 0) {
      paths.push(sb);
      sb = "";
    } else if (c == "[" && expecting.length == 0) {
      expecting.push("]");
    } else if (expecting.length > 0 && expecting[0] == c) {
      expecting.shift();
    } else if (c == "'" || c == '"') {
      expecting.push(c);
    } else {
      sb += c;
    }
  }
  if (sb.length > 0) {
    paths.push(sb);
  }

  return paths;
};

/**
 * Merges the given value object deep into the given root object at the path specified creating any missing path elements
 *
 * @param rootEl  Object to merge values into
 * @param value the value to merge into the root
 * @param path  the json path to merge the value at
 * @return the updated root object
 */
function mergeInto(rootEl: Record<string, any>, value: any, path: string | null) {
  let root = rootEl;
  if (value == null || root == null) {
    return root;
  }
  const location = extractPath(path);
  let point: string | undefined;
  let object = root;
  while ((point = location.shift()) != null) {
    if (point === "$") {
      continue;
    }
    if (location.length == 0 && !isMap(value)) {
      if (!isNullOrUndefined(value)) {
        object[point] = value;
      }
      return root;
    }
    if (Object.prototype.hasOwnProperty.call(object, point)) {
      const current = object[point];
      if (isMap(current)) {
        object = current;
      } else if (Array.isArray(current)) {
        current.push(wrapElement(value, location));
        return root;
      } else {
        //we create an array and add ourselves
        const arr: any[] = [];
        arr.push(current);
        arr.push(wrapElement(value, location));
        object[point] = arr;
      }
    } else {
      const elm = wrapElement(value, location);
      if (!isNullOrUndefined(elm)) {
        object[point] = elm;
      }
      return root;
    }
  }
  //we merge
  if (isMap(value)) {
    Object.assign(object, value);
    //const obj = value;
    //const finalObject = object;
    //Object.entries(obj).forEach(kv => finalObject[kv[0]] = kv[1]);
  }
  return root;
}

const factory = new ComparatorFactory<any>();

function createComparator(type: string | null) {
  let comparator: Comparator<any>;
  if (isNullOrUndefined(type) || "AUTO" === type.toUpperCase()) {
    comparator = factory.compare((a, b) => compareTo(a, b) ?? 0);
  } else {
    switch (type.toUpperCase()) {
      case "NUMBER": {
        comparator = factory.compare((a, b) => {
          if ((isNumberType(a) || typeof a === "string") && (isNumberType(b) || typeof b === "string")) {
            return numberToBigDecimal(a).comparedTo(numberToBigDecimal(b));
          } else if (isNullOrUndefined(a) && !isNullOrUndefined(b)) {
            return -1;
          } else if (!isNullOrUndefined(a) && isNullOrUndefined(b)) {
            return 1;
          }
          return 0;
        });
        break;
      }
      case "BOOLEAN": {
        comparator = factory.compare((a, b) => {
          if (typeof a === "boolean" && typeof b === "boolean") {
            return a === b ? 0 : a ? 1 : -1;
          } else if (isNullOrUndefined(a) && !isNullOrUndefined(b)) {
            return -1;
          } else if (!isNullOrUndefined(a) && isNullOrUndefined(b)) {
            return 1;
          }
          return 0;
        });
        break;
      }
      //case "STRING"
      default: {
        comparator = factory.compareBy(getAsString);
        break;
      }
    }
  }
  return comparator;
}

const VALID_ID_REGEXP = /^[a-z_$][a-z0-9_$]*$/i;

function toObjectFieldPath(key: string) {
  return VALID_ID_REGEXP.test(key) ? "." + key : "[" + JSON.stringify(key) + "]";
}

export {
  isNullOrUndefined,
  isNullOrEmpty,
  isMap,
  getAsString,
  createPayloadResolver,
  isNumberType,
  numberToBigDecimal,
  compareTo,
  createComparator,
  getDocumentContext,
  singleQuotedStringJsonParse,
  isTruthy,
  isEqual,
  mergeInto,
  toObjectFieldPath,
};
