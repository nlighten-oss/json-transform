package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.FunctionHelpers;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

/*
 * For tests
 * @see TransformerFunctionNumberParseTest
 */
@Aliases("numberparse")
@Documentation("Parses a number from string")
@InputType(ArgType.String)
@ArgumentType(value = "pattern", type = ArgType.String, position = 0, defaultString = "#0.00",
              description = "See [tutorial](https://docs.oracle.com/javase/tutorial/i18n/format/decimalFormat.html)")
@ArgumentType(value = "locale", type = ArgType.String, position = 1, defaultIsNull = true,
              description = "Locale to use (language and country specific formatting; set by Java, default is en-US)")
@ArgumentType(value = "grouping", type = ArgType.String, position = 2, defaultIsNull = true,
              description = "A custom character to be used for grouping (default is ,)")
@ArgumentType(value = "decimal", type = ArgType.String, position = 3, defaultIsNull = true,
              description = "A custom character to be used for decimal point (default is .)")
@ArgumentType(value = "radix", type = ArgType.Integer, position = 1, defaultInteger = 10,
              description = "(BASE) Radix to be used in interpreting input")
@OutputType(ArgType.BigDecimal)
public class TransformerFunctionNumberParse<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionNumberParse(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        var pattern = context.getString("pattern");
        if (pattern == null) {
            return new BigDecimal(str);
        }
        if ("BASE".equals(pattern)) {
            var radix = context.getInteger("radix");
            return new BigDecimal(new BigInteger(str, radix));
        }

        var locale = context.getString("locale");
        var resolvedLocale = FunctionHelpers.isNullOrEmpty(locale)
                             ? FunctionHelpers.DEFAULT_LOCALE
                             : Locale.forLanguageTag(locale);
        var grouping = context.getString("grouping");
        var decimal = context.getString("decimal");

        var formatter = FunctionHelpers.getDecimalFormatter(resolvedLocale, pattern, grouping, decimal);
        try {
            return formatter.parse(str);
        } catch (Throwable t) {
            throw t instanceof RuntimeException ex ? ex : new RuntimeException(t);
        }
    }
}
