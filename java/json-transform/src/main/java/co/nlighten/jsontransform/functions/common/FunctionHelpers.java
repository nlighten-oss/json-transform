package co.nlighten.jsontransform.functions.common;

import co.nlighten.jsontransform.adapters.JsonAdapter;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Comparator;
import java.util.Locale;

public class FunctionHelpers {

    public static Locale DEFAULT_LOCALE = Locale.US; // analog to en-US
    public static int NO_SCALE = -1;
    // We try to fit into Decimal128 which supports 34 decimal digits of precision
    // so in principle, we allow 19 digits for the whole number and 15 for the fraction
    public static int MAX_SCALE = 15;
    public static RoundingMode MAX_SCALE_ROUNDING = RoundingMode.HALF_UP;
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static DecimalFormat getDecimalFormatter(Locale locale, String pattern, String groupingSymbol, String decimalSymbol) {
        DecimalFormatSymbols symbols;
        var noGrouping = isNullOrEmpty(groupingSymbol);
        var noDecimal = isNullOrEmpty(decimalSymbol);
        if (noGrouping && noDecimal) {
            symbols = DecimalFormatSymbols.getInstance(locale);
        } else {
            symbols = new DecimalFormatSymbols(locale);
            if (!noGrouping) {
                symbols.setGroupingSeparator(groupingSymbol.charAt(0));
            }
            if (!noDecimal) {
                symbols.setDecimalSeparator(decimalSymbol.charAt(0));
            }
        }
        return new DecimalFormat(pattern, symbols);
    }

    public static Comparator<Object> createComparator(JsonAdapter<?, ?, ?> adapter, String type) {
        return type == null || "AUTO".equals(type)
                ? adapter.comparator()
                : switch (type) {
            case "NUMBER" -> Comparator.comparing(adapter::getNumberAsBigDecimal);
            case "BOOLEAN" -> Comparator.comparing(adapter::getBoolean);
            //case "string"
            default -> Comparator.comparing(adapter::getAsString);
        };
    }
}
