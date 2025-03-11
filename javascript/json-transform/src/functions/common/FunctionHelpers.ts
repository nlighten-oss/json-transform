import BigNumber from "bignumber.js";
//import * as JSONBigInt from "json-bigint";
import { parse, stringify } from "lossless-json";

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

export const BigDecimal = BigNumber.clone({
  // We try to fit into Decimal128 which supports 34 decimal digits of precision
  // so in principle, we allow 19 digits for the whole number and 15 for the fraction
  ROUNDING_MODE: BigNumber.ROUND_HALF_UP,
  DECIMAL_PLACES: 15,
});

export const RoundingModes: Record<string, BigNumber.RoundingMode> = {
  UP: BigNumber.ROUND_UP,
  DOWN: BigNumber.ROUND_DOWN,
  CEILING: BigNumber.ROUND_CEIL,
  FLOOR: BigNumber.ROUND_FLOOR,
  HALF_UP: BigNumber.ROUND_HALF_UP,
  HALF_DOWN: BigNumber.ROUND_HALF_DOWN,
  HALF_EVEN: BigNumber.ROUND_HALF_EVEN,
};

export const NO_SCALE = -1;
// We try to fit into Decimal128 which supports 34 decimal digits of precision
// so in principle, we allow 19 digits for the whole number and 15 for the fraction
export const MAX_SCALE = 15;
export const MAX_SCALE_ROUNDING = BigNumber.ROUND_HALF_UP;

export const DEFAULT_LOCALE = "en-US";
