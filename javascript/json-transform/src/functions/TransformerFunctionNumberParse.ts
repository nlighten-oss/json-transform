import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";
import { BigDecimal, DEFAULT_LOCALE } from "./common/FunctionHelpers";
import { isNullOrEmpty } from "../JsonHelpers";
import { parsePattern } from "./TransformerFunctionNumberFormat";

const DESCRIPTION: FunctionDescription = {
  aliases: ["numberparse"],
  description: "",
  inputType: ArgType.String,
  arguments: {
    pattern: {
      type: ArgType.String,
      position: 0,
      defaultString: "#0.00",
      description: "See [tutorial](https://docs.oracle.com/javase/tutorial/i18n/format/decimalFormat.html)",
    },
    locale: {
      type: ArgType.String,
      position: 1,
      defaultIsNull: true,
      description: "Locale to use (language and country specific formatting; set by Java, default is en-US)",
    },
    grouping: {
      type: ArgType.String,
      position: 2,
      defaultIsNull: true,
      description: "A custom character to be used for grouping (default is ,)",
    },
    decimal: {
      type: ArgType.String,
      position: 3,
      defaultIsNull: true,
      description: "A custom character to be used for decimal point (default is .)",
    },
    radix: {
      type: ArgType.Integer,
      position: 1,
      defaultInteger: 10,
      description: "(BASE) Radix to be used in interpreting input",
    },
  },
  outputType: ArgType.BigDecimal,
};
class TransformerFunctionNumberParse extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return null;
    }
    const pattern = await context.getString("pattern");
    if ("BASE" === pattern) {
      const radix = await context.getInteger("radix");
      return BigDecimal(str, radix ?? 10);
    }

    const locale = await context.getString("locale");
    const resolvedLocale = isNullOrEmpty(locale) ? DEFAULT_LOCALE : locale;
    const groupSeparator = Intl.NumberFormat(resolvedLocale)
      .format(11111)
      .replace(/\p{Number}/gu, "");
    const decimalSeparator = Intl.NumberFormat(resolvedLocale)
      .format(1.1)
      .replace(/\p{Number}/gu, "");

    let normalized = str;
    if (pattern != null) {
      const [format] = parsePattern(pattern, true /* usually negative is the special one */);
      if (format.prefix && normalized.startsWith(format.prefix)) {
        normalized = normalized.substring(format.prefix.length);
      }
      if (format.suffix && normalized.endsWith(format.suffix)) {
        normalized = normalized.substring(0, normalized.length - format.suffix.length);
      }
    }

    const grouping = (await context.getString("grouping")) ?? groupSeparator;
    const decimal = (await context.getString("decimal")) ?? decimalSeparator;

    normalized = normalized.replace(new RegExp(`[${grouping}]`, "g"), "");
    if (decimal !== "." && decimal) {
      normalized = normalized.replace(decimal, ".");
    }

    return BigDecimal(normalized);
  }
}

export default TransformerFunctionNumberParse;
