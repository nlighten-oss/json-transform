package co.nlighten.jsontransform.adapters;

import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonAdapterHelpers {
    private static final String BACKSLASH = "\\";
    public static String singleQuotedStringToDoubleQuoted(String value) {
        return "\"" +
                value.substring(1, value.length() - 1)
                        .replace("\"", BACKSLASH + "\"")
                        .replace(BACKSLASH + "'", "'") +
                "\"";
    }

    public static synchronized <JE, JA extends Iterable<JE>, JO extends JE> boolean trySetArrayAtOOB(JsonArrayAdapter<JE, JA, JO> adapter, JA array, int desiredIndex, JE value, JE fillValue) {
        if (adapter.size(array) <= desiredIndex) {
            for (int i = adapter.size(array); i < desiredIndex; i++) {
                adapter.add(array, fillValue);
            }
            adapter.add(array, value);
            return false;
        }
        return true;
    }

    private static boolean isPrimitiveNumber(final Number n) {
        return n instanceof Integer ||
                n instanceof Byte ||
                n instanceof Short ||
                n instanceof Float ||
                n instanceof Double ||
                n instanceof Long ||
                n instanceof BigDecimal ||
                n instanceof BigInteger;
    }

    private static final BigDecimal BIG_DECIMAL_MAX_INT = BigDecimal.valueOf(Integer.MAX_VALUE);
    private static final BigDecimal BIG_DECIMAL_MAX_LONG = BigDecimal.valueOf(Long.MAX_VALUE);

    public static Number unwrapNumber(final Number n, final boolean reduceBigDecimals) {
        Number unwrapped;

        if (!isPrimitiveNumber(n) || (reduceBigDecimals && n instanceof BigDecimal)) {
            BigDecimal bigDecimal = n instanceof BigDecimal nbd ? nbd : new BigDecimal(n.toString());
            if (bigDecimal.scale() <= 0) {
                if (bigDecimal.abs().compareTo(BIG_DECIMAL_MAX_INT) <= 0) {
                    unwrapped = bigDecimal.intValue();
                } else if (bigDecimal.abs().compareTo(BIG_DECIMAL_MAX_LONG) <= 0){
                    unwrapped = bigDecimal.longValue();
                } else {
                    unwrapped = bigDecimal;
                }
            } else {
                final double doubleValue = bigDecimal.doubleValue();
                if (BigDecimal.valueOf(doubleValue).compareTo(bigDecimal) != 0) {
                    unwrapped = bigDecimal;
                } else {
                    unwrapped = doubleValue;
                }
            }
        } else {
            unwrapped = n;
        }
        return unwrapped;
    }
}
