import {expect} from "vitest";
import BigNumber from "bignumber.js";
import { parse, stringify } from "lossless-json";

// export const JSONBig = (JSONBigInt as any).default({
//   alwaysParseAsBig: true,
// });

const BigDecimalStringifiers = [{
  test: (value: any) => {
    return value instanceof BigNumber || typeof value === "number"|| typeof value === "bigint";
  },
  stringify: (number: any) => {
    return (number as BigNumber).toString();
  }
}];
export const JSONBig = {
  parse: (text: string): any => parse(text, null, BigNumber),
  stringify: (value: any): string => stringify(value, null, undefined, BigDecimalStringifiers) ?? "undefined",
};

function areURLSearchParamsEqual(params1: URLSearchParams, params2: URLSearchParams): boolean {
  if (params1.toString() === params2.toString()) {
    return true;
  }

  const keys1 = Array.from(params1.keys()).sort();
  const keys2 = Array.from(params2.keys()).sort();

  if (keys1.length !== keys2.length) {
    return false;
  }

  return keys1.every(key => params1.getAll(key).sort().toString() === params2.getAll(key).sort().toString());
}

// import * as JsonBigInt from "json-bigint";
//
// export const JSONBig = (JsonBigInt as any).default({
//   alwaysParseAsBig: true,
// });

const hasOwn = Object.prototype.hasOwnProperty;

const PODS = {
  javascript: "http://localhost:10002/api/v1/transform",
  javaGson: "http://localhost:10000/api/v1/transform/gson",
  javaJackson: "http://localhost:10000/api/v1/transform/jackson",
  javaJsonOrg: "http://localhost:10000/api/v1/transform/jsonorg",
  javaPojo: "http://localhost:10000/api/v1/transform/pojo"
}

const callTransform = async (given: any, platformToTest: keyof typeof PODS) => {
  return fetch(PODS[platformToTest], {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSONBig.stringify(given)
  });
}

const getTypeOf = (value: any) => {
  if (typeof value === 'object') {
    return value.constructor.name;
  }
  return typeof value;
}

export const assertTransformation = async (t: any, platform: keyof typeof PODS) => {
  return callTransform(t.given, platform)
    .then(res => res.text())
    .then(d => {
      const data = JSONBig.parse(d);
      const message = `${t.name} (actual type: ${typeof data.result}${typeof t.expect.equal !== 'undefined' ? `, expected type: ${getTypeOf(t.expect.equal)}` : ""})`;
      if (hasOwn.call(t.expect, "isNull") && t.expect.isNull === true) {
        expect(data.result ?? null, message).toBeNull();
      }
      if (hasOwn.call(t.expect, "equal")) {
        if (t.expect.ignoreOrder && Array.isArray(t.expect.equal)) {
          expect(data.result, message).toHaveLength(t.expect.equal.length);
          expect(data.result, message).toEqual(expect.arrayContaining(t.expect.equal));
        } else if (t.expect.urlSearchParams === true && typeof t.expect.equal === "string" && typeof data.result === "string") {
          const expectedParams = new URLSearchParams(t.expect.equal);
          const resultParams = new URLSearchParams(data.result);
          expect(areURLSearchParamsEqual(expectedParams, resultParams), message).toBeTruthy();
        } else{
          expect(data.result, message).toEqual(t.expect.equal);
        }
      }
      if (hasOwn.call(t.expect, "notEqual")) {
        expect(data.result, message).not.toEqual(t.expect.notEqual);
      }
      if (hasOwn.call(t.expect, "length")) {
        expect(data.result, message).toHaveLength(t.expect.length);
      }
      if (hasOwn.call(t.expect, "type")) {
        if (t.expect.type === "array") {
          expect(data.result, message).toBeInstanceOf(Array);
        } else {
          expect(data.result, message).toBeTypeOf(t.expect.type);
        }
      }
      if (hasOwn.call(t.expect, "format")) {
        switch (t.expect.format) {
          case "date-time":
            expect(isNaN(new Date(data.result).getDate()), message).not.toBeTruthy();
            break
          default:
            expect.fail("Unknown format: " + t.expect.format,"", message);
        }
      }
    });
}