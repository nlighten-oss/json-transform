import type { IncomingMessage, ServerResponse } from "http";
import { parse, stringify } from "lossless-json";
import { BigDecimal } from "../src/functions/common/FunctionHelpers";
import BigNumber from "bignumber.js";

// export const JSONBig = (JSONBigInt as any).default({
//   alwaysParseAsBig: true,
// });

const BigDecimalStringifiers = [
  {
    test: (value: any) => {
      return (
        value instanceof BigDecimal ||
        value instanceof BigNumber ||
        typeof value === "number" ||
        typeof value === "bigint"
      );
    },
    stringify: (number: any) => {
      return (number as BigNumber).toString();
    },
  },
];
export const JSONBig = {
  parse: (text: string): any => parse(text, null, BigDecimal),
  stringify: (value: any, replacer: any = null): string =>
    stringify(value, replacer, undefined, BigDecimalStringifiers) ?? "undefined",
};

// import * as JSONBigInt from "json-bigint";
//
// export const JSONBig = (JSONBigInt as any).default({
//   alwaysParseAsBig: true,
// });

export const HEADER_ContentType = "Content-Type";
export const ContentType_JSON = "application/json";
export const ContentType_HTML = "text/html";
export const HTMLHeaders = { [HEADER_ContentType]: ContentType_HTML };
export const JSONHeaders = { [HEADER_ContentType]: ContentType_JSON };

export const parseBody = (req: IncomingMessage): Promise<any> =>
  new Promise((resolve, reject) => {
    let body = "";
    req.on("data", (chunk: any) => {
      body += chunk.toString();
    });
    req.on("end", () => {
      try {
        resolve(JSONBig.parse(body));
      } catch (e) {
        reject(e);
      }
    });
  });

export const send = (res: ServerResponse, statusCode: number, headers: Record<string, string>, data?: any) => {
  res.writeHead(statusCode, headers);
  if (typeof data !== "undefined" && headers?.[HEADER_ContentType] === ContentType_JSON) {
    res.end(JSONBig.stringify(data));
  } else {
    res.end(data);
  }
};
