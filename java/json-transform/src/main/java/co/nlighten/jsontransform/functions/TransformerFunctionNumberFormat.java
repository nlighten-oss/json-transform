package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

public class TransformerFunctionNumberFormat extends TransformerFunction {
    public TransformerFunctionNumberFormat() {
        super(FunctionDescription.of(
            Map.of(
            "type", ArgumentType.of(ArgType.Enum).position(0).defaultEnum("NUMBER"),
            "locale", ArgumentType.of(ArgType.String).position(1).defaultIsNull(true),
            "compact_style", ArgumentType.of(ArgType.Enum).position(2).defaultEnum("SHORT"),
            "pattern", ArgumentType.of(ArgType.String).position(2).defaultString("#0.00"),
            "grouping", ArgumentType.of(ArgType.String).position(3).defaultIsNull(true),
            "decimal", ArgumentType.of(ArgType.String).position(4).defaultIsNull(true),
            "radix", ArgumentType.of(ArgType.Integer).position(1).defaultInteger(10),
            "currency", ArgumentType.of(ArgType.String).position(2).defaultIsNull(true)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
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
            default -> NumberFormat.getNumberInstance(FunctionHelpers.DEFAULT_LOCALE);
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
