import { add, addMilliseconds, format, formatISO, fromUnixTime, sub, subMilliseconds, parseJSON } from "date-fns";
import { tz, TZDate } from "@date-fns/tz";
import BigNumber from "bignumber.js";
import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { BigDecimal } from "./common/FunctionHelpers";

const ChronoUnitToDuration: Record<string, string> = {
  SECONDS: "seconds",
  MINUTES: "minutes",
  HOURS: "hours",
  DAYS: "days",
  MONTHS: "months",
  YEARS: "years",
};

const ISO_TRIM = /\.000(Z|[+-]\d\d:)/;
const MAX_TRIM = /\.?0+(Z|[+-]\d\d:)/;
/**
 *
 * @param date date to format
 * @param maxTrimmed if true, trim with min width of 0, otherwise, render milliseconds in 3 digits as usual unless it's 0
 */
const toISOString = (date: Date, maxTrimmed?: boolean) => {
  const iso = date.toISOString();
  return iso.replace(maxTrimmed ? MAX_TRIM : ISO_TRIM, "$1");
};

class TransformerFunctionDate extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        scale: { type: ArgType.Integer, position: 0, defaultInteger: -1 },
        format: { type: ArgType.Enum, position: 0, defaultEnum: "ISO" },
        digits: { type: ArgType.Integer, position: 1, defaultInteger: -1 },
        units: { type: ArgType.Enum, position: 1 },
        amount: { type: ArgType.Long, position: 2, defaultLong: 0 },
        resolution: { type: ArgType.Enum, position: 1, defaultEnum: "UNIX" },
        pattern: { type: ArgType.String, position: 1 },
        timezone: { type: ArgType.String, position: 2, defaultString: "UTC" },
        zone: { type: ArgType.String, position: 1, defaultString: "UTC" },
      },
    });
  }

  private static parseInstant(value: any): Date {
    if (value instanceof Date) return value;
    if (typeof value === "string") {
      if (value.includes("T") || value.includes("-")) return parseJSON(value);
      if (value.includes(":")) return parseJSON(`1970-01-01T${value}`);
      value = parseInt(value);
    }
    if (typeof value === "number") {
      if (value < 2671726769) {
        return fromUnixTime(value);
      }
      return new Date(value);
    }
    return parseJSON(value);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const unwrapped = await context.getUnwrapped(null);
    if (unwrapped == null) {
      return null;
    }
    const instant = TransformerFunctionDate.parseInstant(unwrapped);
    switch (await context.getEnum("format")) {
      case "ISO": {
        switch (await context.getInteger("digits")) {
          case 0:
            return instant.toISOString().replace(/\.\d+/, ""); // no second fractions
          case 3:
            return instant.toISOString(); // milliseconds
          case 6:
            return instant.toISOString().replace("Z", "000Z"); // microseconds
          case 9:
            return instant.toISOString().replace("Z", "000000Z"); // nanoseconds
          default:
            return toISOString(instant);
        }
      }
      case "GMT":
        return instant.toUTCString();
      case "ADD": {
        const units = await context.getEnum("units");
        if (units && ChronoUnitToDuration[units]) {
          return toISOString(add(instant, { [ChronoUnitToDuration[units]]: await context.getInteger("amount") }));
        }
        switch (units) {
          case "HALF_DAYS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return toISOString(add(instant, { hours: amount * 12 }));
          }
          case "MILLIS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return toISOString(addMilliseconds(instant, amount));
          }
          case "NANOS":
          case "MICROS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return toISOString(addMilliseconds(instant, amount / (units === "NANOS" ? 1e6 : 1e3)));
          }
        }
        return null;
      }
      case "SUB": {
        const units = await context.getEnum("units");
        if (units && ChronoUnitToDuration[units]) {
          return toISOString(sub(instant, { [ChronoUnitToDuration[units]]: await context.getInteger("amount") }));
        }
        switch (units) {
          case "HALF_DAYS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return toISOString(sub(instant, { hours: amount * 12 }));
          }
          case "MILLIS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return toISOString(subMilliseconds(instant, amount));
          }
          case "NANOS":
          case "MICROS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return toISOString(subMilliseconds(instant, amount / (units === "NANOS" ? 1e6 : 1e3)));
          }
        }
        return null;
      }
      case "DATE": {
        return formatISO(instant, { representation: "date" });
      }
      case "EPOCH": {
        switch (await context.getEnum("resolution")) {
          case "MS":
            return BigDecimal(instant.getTime());
          default:
            return BigDecimal(instant.getTime()).dividedBy(1000).integerValue(BigNumber.ROUND_FLOOR);
        }
      }
      case "FORMAT": {
        const timeZone = await context.getString("timezone");
        const pattern = (await context.getString("pattern")) ?? "";
        if (timeZone) {
          return format(tz(timeZone)(instant), pattern); //(instant) formatInTimeZone(instant, timeZone, pattern);
        }
        return format(instant, pattern);
      }
      case "ZONE": {
        const zone = await context.getString("zone");
        const iso = toISOString(instant);
        if (!zone) return iso;
        return toISOString(TZDate.tz(zone, instant), true);
      }
      default:
        return null;
    }
  }
}

export default TransformerFunctionDate;
