import {expect} from "vitest";
import BigNumber from "bignumber.js";
import { parse, stringify } from "lossless-json";
import {Implementation, ImplUrls} from "./implementations";
import { JsonTransformExample } from "@nlighten/json-transform-core"

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

const callTransform = async (given: any, impl: Implementation) => {
  return fetch(ImplUrls[impl], {
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

export const assertTransformation = async (t: JsonTransformExample, impl: Implementation) => {
  //correct given input by format
  if (t.given.inputFormat){
    if (t.given.inputFormat === "big-decimal") {
      t.given.input = BigNumber(t.given.input);
    }
  }

  return callTransform(t.given, impl)
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
        } else if (t.expect.format === "url-search-params" && typeof t.expect.equal === "string" && typeof data.result === "string") {
          const expectedParams = new URLSearchParams(t.expect.equal);
          const resultParams = new URLSearchParams(data.result);
          expect(areURLSearchParamsEqual(expectedParams, resultParams), message).toBeTruthy();
        } else if (t.expect.format === "big-decimal" && typeof t.expect.equal === "string") {
          const expectedValue = BigNumber(t.expect.equal);
          expect(data.result, message).toEqual(expectedValue);
        } else{
          expect(data.result, message).toEqual(t.expect.equal);
        }
      } else if (typeof t.expect.format !== 'undefined') {
        switch (t.expect.format) {
          case "date-time":
            expect(isNaN(new Date(data.result).getDate()), message).not.toBeTruthy();
            break
          default:
            expect.fail("Unknown format: " + t.expect.format,"", message);
        }
      }
      if (typeof t.expect.notEqual !== 'undefined') {
        expect(data.result, message).not.toEqual(t.expect.notEqual);
      }
      if (typeof t.expect.length !== 'undefined') {
        expect(data.result, message).toHaveLength(t.expect.length);
      }
      if (typeof t.expect.type !== 'undefined') {
        if (t.expect.type === "array") {
          expect(data.result, message).toBeInstanceOf(Array);
        } else {
          expect(data.result, message).toBeTypeOf(t.expect.type);
        }
      }
    });
}