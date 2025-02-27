import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { DEFAULT_LOCALE } from "./common/FunctionHelpers";
import { isNullOrEmpty } from "../JsonHelpers";
import BigNumber from "bignumber.js";
import Format = BigNumber.Format;

const EuropeCountries = new Set([
  "AT",
  "BE",
  "BG",
  "CY",
  "CZ",
  "DE",
  "DK",
  "EE",
  "ES",
  "FI",
  "FR",
  "GR",
  "HR",
  "HU",
  "IE",
  "IT",
  "LT",
  "LU",
  "LV",
  "MT",
  "NL",
  "PL",
  "PT",
  "RO",
  "SE",
  "SI",
  "SK",
]);
function getDefaultCurrency(locale: string) {
  const country = locale.split("-").pop() as string;
  if (country === "GB") {
    return "GBP";
  }
  if (EuropeCountries.has(country)) {
    return "EUR";
  }
  if (country.length === 2) {
    return country + "D";
  }
  return "USD";
}

const maskRegex = /[0-9\-+#]/;
export const parsePattern = function (
  pattern: string,
  isNegative?: boolean,
): [format: Format, decimalPlaces: number | null, integerPlaces: number | null] {
  const parts = pattern.split(";");
  const mask = parts.length < 2 ? parts[0] : isNegative ? parts[1] : parts[0];

  // find prefix
  const start = mask.search(maskRegex);
  const prefix = start > 0 ? mask.substring(0, start) : undefined;

  // find suffix
  const end = mask.split("").reverse().join("").search(maskRegex);
  const endOffset = mask.length - end;
  const endSubstr = mask.substring(endOffset, endOffset + 1);
  // Add 1 to offset if mask has a trailing decimal/comma
  const endIndex = endOffset + (endSubstr === "." || endSubstr === "," ? 1 : 0);
  const suffix = end > 0 ? mask.substring(endIndex, mask.length) : undefined;

  let integer = "";
  // decimal places
  const dotIndex = mask.indexOf(".");
  let decimalPlaces: number | null = null;
  if (dotIndex < 0) {
    integer = mask;
    decimalPlaces = 0;
  } else {
    integer = mask.substring(start, dotIndex);
    const fraction = mask.substring(dotIndex + 1);
    const digitsAfterDot = fraction.match(/^[0, ]+/g)?.[0]?.replace(/[, ]+/g, "")?.length ?? 0;
    if (fraction[digitsAfterDot] !== "#") {
      decimalPlaces = digitsAfterDot;
    }
  }
  // format
  const format: Format = {
    prefix,
    suffix,
  };

  const commaIndex = integer.lastIndexOf(",");
  format.groupSize = commaIndex < 0 ? undefined : integer.length - commaIndex - 1;

  const integerPlaces = integer.match(/[0, ]+$/g)?.[0]?.replace(/[, ]+/g, "")?.length ?? null;

  return [format, decimalPlaces, integerPlaces];
};

const RemoveTrailingZerosRegExp = /\.?0+$/;

class TransformerFunctionNumberFormat extends TransformerFunction {
  private readonly defaultFormat: Format;

  constructor() {
    super({
      arguments: {
        type: { type: ArgType.Enum, position: 0, defaultEnum: "NUMBER" },
        locale: { type: ArgType.String, position: 1, defaultIsNull: true },
        compact_style: { type: ArgType.Enum, position: 2, defaultEnum: "SHORT" },
        pattern: { type: ArgType.String, position: 2, defaultString: "#0.00" },
        grouping: { type: ArgType.String, position: 3, defaultIsNull: true },
        decimal: { type: ArgType.String, position: 4, defaultIsNull: true },
        radix: { type: ArgType.Integer, position: 1, defaultInteger: 10 },
        currency: { type: ArgType.String, position: 2, defaultIsNull: true },
      },
    });
    const [format] = parsePattern("#,##0.000");
    format.decimalSeparator = ".";
    format.groupSeparator = ",";
    format.fractionGroupSize = 0;
    this.defaultFormat = format;
  }

  override async apply(context: FunctionContext): Promise<any> {
    const type = await context.getEnum("type");
    const input = await context.getBigDecimal(null);
    if (input === null) {
      return null;
    }

    if ("BASE" === type) {
      const radix = await context.getInteger("radix");
      return input.integerValue(BigNumber.ROUND_FLOOR).toString(radix ?? 10);
    }

    const locale = await context.getEnum("locale");
    const resolvedLocale = isNullOrEmpty(locale) ? DEFAULT_LOCALE : locale;

    switch (type) {
      case "DECIMAL": {
        const pattern = await context.getString("pattern");
        const grouping = await context.getString("grouping");
        const decimal = await context.getString("decimal");

        const groupSeparator = Intl.NumberFormat(resolvedLocale)
          .format(11111)
          .replace(/\p{Number}/gu, "");
        const decimalSeparator = Intl.NumberFormat(resolvedLocale)
          .format(1.1)
          .replace(/\p{Number}/gu, "");

        const [format, decimalPlaces, integerPlaces] = parsePattern(pattern ?? "#0.00", input.isNegative());
        format.decimalSeparator = decimal ?? decimalSeparator;
        format.groupSeparator = grouping ?? groupSeparator;
        return typeof decimalPlaces === "number" ? input?.toFormat(decimalPlaces, format) : input.toFormat(format);
      }
      case "CURRENCY": {
        return new Intl.NumberFormat(resolvedLocale, {
          style: "currency",
          currency: (await context.getEnum("currency")) ?? getDefaultCurrency(resolvedLocale),
        }).format(input.toString() as any);
      }
      case "PERCENT": {
        return new Intl.NumberFormat(resolvedLocale, { style: "percent" }).format(input.toString() as any);
      }
      case "INTEGER": {
        return new Intl.NumberFormat(resolvedLocale, { style: "decimal" }).format(input.toFixed(0) as any);
      }
      case "COMPACT": {
        const compactStyle = await context.getEnum("compact_style");
        return new Intl.NumberFormat(resolvedLocale, {
          notation: "compact",
          compactDisplay: compactStyle === "LONG" ? "long" : "short",
        }).format(input.toString() as any);
      }
      default: {
        return input.toFormat(3, BigNumber.ROUND_UP, this.defaultFormat).replace(RemoveTrailingZerosRegExp, "");
      }
    }
  }
}

export default TransformerFunctionNumberFormat;
