import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { BigDecimal, DEFAULT_LOCALE } from "./common/FunctionHelpers";
import { isNullOrEmpty } from "../JsonHelpers";
import { parsePattern } from "./TransformerFunctionNumberFormat";

class TransformerFunctionNumberParse extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        pattern: { type: ArgType.String, position: 0, defaultString: "#0.00" },
        locale: { type: ArgType.String, position: 1, defaultIsNull: true },
        grouping: { type: ArgType.String, position: 2, defaultIsNull: true },
        decimal: { type: ArgType.String, position: 3, defaultIsNull: true },
        radix: { type: ArgType.Integer, position: 1, defaultInteger: 10 },
      },
    });
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
