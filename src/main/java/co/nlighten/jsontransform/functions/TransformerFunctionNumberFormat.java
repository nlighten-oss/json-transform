package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.FunctionHelpers;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

import java.text.NumberFormat;
import java.util.Locale;

/*
 * For tests
 * @see TransformerFunctionNumberFormatTest
 */
@Aliases("numberformat")
@Documentation("Formats a number")
@InputType(ArgType.BigDecimal)
@ArgumentType(value = "type", type = ArgType.Enum, position = 0, defaultEnum = "NUMBER",
              enumValues = { "NUMBER", "DECIMAL", "CURRENCY", "PERCENT", "INTEGER", "COMPACT", "BASE" },
              description = "Type of output format")
@ArgumentType(value = "locale", type = ArgType.String, position = 1, defaultIsNull = true,
              description = "Locale to use (language and country specific formatting; set by Java, default is en-US)")
@ArgumentType(value = "compact_style", type = ArgType.Enum, position = 2, defaultEnum = "SHORT",
              enumValues = { "SHORT", "LONG" },
              description = "(COMPACT) Type of compacting format")
@ArgumentType(value = "pattern", type = ArgType.String, position = 2, defaultString = "#0.00",
              description = "(DECIMAL) See [tutorial](https://docs.oracle.com/javase/tutorial/i18n/format/decimalFormat.html)")
@ArgumentType(value = "grouping", type = ArgType.String, position = 3, defaultIsNull = true,
              description = "(DECIMAL) A custom character to be used for grouping (default is ,)")
@ArgumentType(value = "decimal", type = ArgType.String, position = 4, defaultIsNull = true,
              description = "(DECIMAL) A custom character to be used for decimal point (default is .)")
@ArgumentType(value = "radix", type = ArgType.Integer, position = 1, defaultInteger = 10,
              description = "(BASE) Radix to be used for formatting input")
@OutputType(ArgType.String)
public class TransformerFunctionNumberFormat<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionNumberFormat(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var type = context.getEnum("type");
        var input = context.getBigDecimal(null);

        if ("BASE".equals(type)) {
            return input.toBigInteger().toString(context.getInteger("radix"));
        }

        var locale = context.getEnum("locale");
        var resolvedLocale = FunctionHelpers.isNullOrEmpty(locale)
                             ? FunctionHelpers.DEFAULT_LOCALE
                             : Locale.forLanguageTag(locale);

        var formatter = switch (type) {
            case "DECIMAL" -> FunctionHelpers.getDecimalFormatter(
                    resolvedLocale,
                    context.getString("pattern"),
                    context.getString("grouping"),
                    context.getString("decimal")
                );
            case "CURRENCY" -> NumberFormat.getCurrencyInstance(resolvedLocale);
            case "PERCENT" -> NumberFormat.getPercentInstance(resolvedLocale);
            case "INTEGER" -> NumberFormat.getIntegerInstance(resolvedLocale);
            case "COMPACT" -> NumberFormat.getCompactNumberInstance(
                    resolvedLocale,
                    NumberFormat.Style.valueOf(context.getEnum("compact_style"))
                );
            default -> NumberFormat.getNumberInstance();
        };
        return formatter.format(input);
    }
}
