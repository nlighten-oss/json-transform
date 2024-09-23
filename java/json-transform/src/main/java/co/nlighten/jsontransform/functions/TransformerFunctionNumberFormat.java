package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.FunctionHelpers;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/*
 * For tests
 * @see TransformerFunctionNumberFormatTest
 */
@ArgumentType(value = "type", type = ArgType.Enum, position = 0, defaultEnum = "NUMBER")
@ArgumentType(value = "locale", type = ArgType.String, position = 1, defaultIsNull = true)
@ArgumentType(value = "compact_style", type = ArgType.Enum, position = 2, defaultEnum = "SHORT")
@ArgumentType(value = "pattern", type = ArgType.String, position = 2, defaultString = "#0.00")
@ArgumentType(value = "grouping", type = ArgType.String, position = 3, defaultIsNull = true)
@ArgumentType(value = "decimal", type = ArgType.String, position = 4, defaultIsNull = true)
@ArgumentType(value = "radix", type = ArgType.Integer, position = 1, defaultInteger = 10)
@ArgumentType(value = "currency", type = ArgType.String, position = 2, defaultIsNull = true)
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
            case "CURRENCY" -> getCurrencyFormatter(
                    resolvedLocale,
                    context.getString("currency")
                );
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

    private NumberFormat getCurrencyFormatter(Locale resolvedLocale, String currency) {
        var nf = NumberFormat.getCurrencyInstance(resolvedLocale);
        if (!FunctionHelpers.isNullOrEmpty(currency)) {
            nf.setCurrency(Currency.getInstance(currency));
        }
        return nf;
    }
}
