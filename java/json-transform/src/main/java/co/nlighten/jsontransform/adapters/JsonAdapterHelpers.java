package co.nlighten.jsontransform.adapters;

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
}
