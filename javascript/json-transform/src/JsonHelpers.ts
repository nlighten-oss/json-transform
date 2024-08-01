import { v4 as uuidv4 } from 'uuid';
import DocumentContext from "./DocumentContext";
import {BigDecimal} from "./functions/common/FunctionHelpers";
import {areSimilar} from "@nlighten/json-schema-utils";

const JSONPATH_ROOT = "$",
  JSONPATH_ROOT_ESC = "\\$",
  JSONPATH_ROOT_CHAR = JSONPATH_ROOT.charAt(0),
  JSONPATH_ALT_PREFIX = "#",
  JSONPATH_ALT_PREFIX_ESC = "\\#";

const isNullOrUndefined = (value: any) : value is null | undefined => value == null || typeof value === 'undefined';

const getAsString = (value: any) : null | string => {
  if (isNullOrUndefined(value)) {
    return null;
  }
  if (typeof value === 'string') {
    return value;
  }
  if (typeof value === 'number') {
    return value.toString();
  }
  if (typeof value === 'boolean') {
    return value ? 'true' : 'false';
  }
  return JSON.stringify(value);
}

const numberCompare = (a: number, b: number) => {
  return a < b ? -1 : (a === b ? 0 : 1);
}

const numberType = (a: any) => typeof a === 'number' || typeof a === 'bigint' || a instanceof BigDecimal;

const compareTo = (a: any, b: any) => {
  if (Array.isArray(a) && Array.isArray(b)) {
    return numberCompare(a.length, b.length);
  } else if (a && b && typeof a === 'object' && typeof b === 'object') {
    return numberCompare(Object.keys(a).length, Object.keys(b).length);
  } else if (typeof a === 'string' && typeof b === 'string') {
    return a.localeCompare(b);
  } else if (numberType(a) && numberType(b)) {
    return BigDecimal(a).comparedTo(BigDecimal(b));
  } else if (typeof a === 'boolean' && typeof b === 'boolean') {
    return a === b ? 0 : (a ? 1 : -1);
  } else if (isNullOrUndefined(a) && !isNullOrUndefined(b)) {
    return -1;
  } else if (!isNullOrUndefined(a) && isNullOrUndefined(b)) {
    return 1;
  }
  // incomparable
  return null;
}

const getDocumentContext = (value: any) : DocumentContext => {
  return new DocumentContext(value);
}

const PRIMITIVE_TYPES_SET = new Set(['string', 'number', 'boolean', 'bigint']);
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
  const indexOfDot = path.indexOf('.');
  const indexOfIndexer = path.indexOf('[');
  const endOfKeyIndex = indexOfDot > -1 && indexOfIndexer > -1 ? Math.min(indexOfDot, indexOfIndexer) : indexOfDot > -1 ? indexOfDot : indexOfIndexer;
  return endOfKeyIndex < 0 ? path : path.substring(0, endOfKeyIndex);
}

const createPayloadResolver = (payload: any, additionalContext: Record<string, any>) => {
  const json = getDocumentContext(payload);
  const additionalJsons = !additionalContext ? {} : Object.entries(additionalContext).reduce((a, [key, value]) => {
    if (PRIMITIVE_TYPES_SET.has(typeof value)) {
      a[key] = value;
    } else {
      a[key] = new DocumentContextSupplier(value);
    }
    return a;
  }, {} as Record<string, any>);

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
      if (name.length < 2 ||
        // fix for regex patterns being detected (e.g. $0)
        (name.charAt(1) != JSONPATH_ROOT_CHAR && !IS_DIGIT_REGEX.test(name.charAt(1)))) {
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
    }
  }
}

const lenientJsonParse = (input: string) => {
  // TODO: replace with a non exploitable solution
  const f = new Function(`return ${input}`);
  return f();
}

const BIGINT_ZERO = BigInt(0);
const isTruthy = (value: any, javascriptStyle?: boolean) => {
  if (typeof value === 'boolean') {
    return value;
  } else if (typeof value === 'number') {
    return value != 0;
  } else if (typeof value === 'bigint') {
    return value !== BIGINT_ZERO;
  } else if (value instanceof BigDecimal) {
    return !value.isZero();
  } else if (typeof value === 'string') {
    return javascriptStyle ? Boolean(value) : value.toLowerCase() === 'true';
  } else if (Array.isArray(value)) {
    return value.length > 0;
  } else if (value && typeof value === 'object') {
    return Object.keys(value).length > 0;
  }
  return !isNullOrUndefined(value);
}

const isEqual = (value: any, other: any): boolean => {
  if (value === other) {
    return true;
  }
  if (numberType(value) && numberType(other)) {
    return BigDecimal(value).eq(BigDecimal(other));
  }
  return areSimilar(value, other);
}


export {
  isNullOrUndefined,
  createPayloadResolver,
  getAsString,
  compareTo,
  getDocumentContext,
  lenientJsonParse,
  isTruthy,
  isEqual
};