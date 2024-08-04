import { add, addMilliseconds, format, fromUnixTime, sub, subMilliseconds, parseJSON } from "date-fns";
import { formatInTimeZone, format as formatEx } from "date-fns-tz";
import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";
import { BigDecimal, MAX_SCALE_ROUNDING, RoundingModes } from "./common/FunctionHelpers";
import BigNumber from "bignumber.js";

const ChronoUnitToDuration: Record<string, string> = {
  SECONDS: "seconds",
  MINUTES: "minutes",
  HOURS: "hours",
  DAYS: "days",
  MONTHS: "months",
  YEARS: "years",
};

const DESCRIPTION: FunctionDescription = {
  aliases: ["date"],
  description: "",
  inputType: ArgType.String,
  arguments: {
    scale: {
      type: ArgType.Integer,
      position: 0,
      defaultInteger: -1,
      description: "Scale of BigDecimal to set (default is 10 max)",
    },
    format: {
      type: ArgType.Enum,
      position: 0,
      defaultEnum: "ISO",
      enumValues: ["ISO", "GMT", "DATE", "ADD", "SUB", "EPOCH", "FORMAT", "ZONE"],
      description: "Formatter to use",
    },
    digits: {
      type: ArgType.Integer,
      position: 1,
      defaultInteger: -1,
      description: "(ISO) precision for time part (scale) 0|3|6|9|-1",
    },
    units: {
      type: ArgType.Enum,
      position: 1,
      required: true,
      enumValues: ["NANOS", "MICROS", "MILLIS", "SECONDS", "MINUTES", "HOURS", "HALF_DAYS", "DAYS", "MONTHS", "YEARS"],
      description: "(ADD/SUB) Units to use (ChronoUnit)",
    },
    amount: {
      type: ArgType.Long,
      position: 2,
      defaultLong: 0,
      description: "(ADD/SUB) Amount of units to add or subtract (can be negative)",
    },
    resolution: {
      type: ArgType.Enum,
      position: 1,
      defaultEnum: "UNIX",
      enumValues: ["UNIX", "MS"],
      description: "(EPOCH) Resolution of epoch (Seconds or Milliseconds)",
    },
    pattern: { type: ArgType.String, position: 1, required: true, description: "(FORMAT) Pattern to use" },
    timezone: { type: ArgType.String, position: 2, defaultString: "UTC", description: "(FORMAT)" },
    zone: { type: ArgType.String, position: 1, defaultString: "UTC", description: "(ZONE)" },
  },
  outputType: [ArgType.String, ArgType.Integer],
};
class TransformerFunctionDate extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
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
            return instant.toISOString();
        }
      }
      case "GMT":
        return instant.toUTCString();
      case "ADD": {
        const units = await context.getEnum("units");
        if (units && ChronoUnitToDuration[units]) {
          return add(instant, { [ChronoUnitToDuration[units]]: await context.getInteger("amount") }).toISOString();
        }
        switch (units) {
          case "HALF_DAYS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return add(instant, { hours: amount * 12 }).toISOString();
          }
          case "MILLIS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return addMilliseconds(instant, amount).toISOString();
          }
          case "NANOS":
          case "MICROS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return addMilliseconds(instant, amount / (units === "NANOS" ? 1e6 : 1e3)).toISOString();
          }
        }
        return null;
      }
      case "SUB": {
        const units = await context.getEnum("units");
        if (units && ChronoUnitToDuration[units]) {
          return sub(instant, { [ChronoUnitToDuration[units]]: await context.getInteger("amount") }).toISOString();
        }
        switch (units) {
          case "HALF_DAYS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return sub(instant, { hours: amount * 12 }).toISOString();
          }
          case "MILLIS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return subMilliseconds(instant, amount).toISOString();
          }
          case "NANOS":
          case "MICROS": {
            const amount = (await context.getInteger("amount")) ?? 0;
            return subMilliseconds(instant, amount / (units === "NANOS" ? 1e6 : 1e3)).toISOString();
          }
        }
        return null;
      }
      case "DATE": {
        return instant.toISOString().substring(0, 10);
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
          return formatInTimeZone(instant, timeZone, pattern);
        }
        return format(instant, pattern);
      }
      case "ZONE": {
        const zone = await context.getString("zone");
        const iso = instant.toISOString();
        if (!zone) return iso;
        const fmt = formatInTimeZone(instant, zone, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return /\.0+Z$/.test(iso) ? fmt.replace(/\.0+/, "") : fmt;
      }
      default:
        return null;
    }
  }
}

export default TransformerFunctionDate;
