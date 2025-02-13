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
}
