package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class JsonTransformerUtils {

    private static Pattern variableDetectionRegExp = variableDetectionRegExpFactory(null, null);
    static final Pattern validIdRegExp = Pattern.compile("^[a-zA-Z_$][a-zA-Z0-9_$]*$");

    public static Pattern variableDetectionRegExpFactory(Integer flags, List<String> altNames) {
        var altPrefixes =  altNames != null && !altNames.isEmpty()
                ? "(" + String.join("|", altNames) + ")?"
                : "";
        return Pattern.compile(
                "(?<![\\])}?!@#$%^&*+\\\\\\w])(#[a-z_]+[a-z_\\d]*|\\$(?!\\$)" + altPrefixes + ")(((\\.(?![-\\w$]+\\()[-\\w$]+)|(\\[[^\\]\\n]+]))+|(?=[^\\w.]|$))",
                flags != null ? flags : 0);
    }

    private static void findAllVariableUses(JsonAdapter<?, ?, ?> adapter, Object element, Map<String, Object> result, String path) {
        if (adapter.isJsonString(element)) {
            var string = adapter.getAsString(element);
            var matcher = variableDetectionRegExp.matcher(string);
            while (matcher.find()) {
                var v = matcher.group();
                if (!result.containsKey(v)) result.put(v, path);
            }
        } else if (adapter.isJsonArray(element)) {
            var size = adapter.size(element);
            for (var i = 0; i < size; i++) {
                findAllVariableUses(adapter, adapter.get(element, i), result, path + "[" + i + "]");
            }
        } else if (adapter.isJsonObject(element)) {
            var isObjectFunction = adapter.keySet(element).stream().anyMatch(x -> x.startsWith("$$"));
            adapter.keySet(element).forEach(x -> {
                var value = adapter.get(element, x);
                findAllVariableUses(adapter, value, result,
                        JsonTransformer.OBJ_DESTRUCT_KEY.equals(x) || isObjectFunction
                                ? path
                                : path + "." + x
                );
            });
        }
    }

    /**
     * Find all variable uses in the JSON element.
     * @param adapter the JSON adapter to use
     * @param element the JSON element to search
     * @return a map of variable uses to their paths
     */
    public static Map<String, Object> findAllVariableUses(JsonAdapter<?, ?, ?> adapter, Object element) {
        var result = new HashMap<String, Object>();
        findAllVariableUses(adapter, element, result, "$");
        return result;
    }

    /**
     * Set the regular expression used to detect variables in the JSON string.
     * @param flags the flags to compile the regular expression with
     * @param altNames alternative names for the variable prefix
     */
    public static void setVariableDetectionRegExp(Integer flags, List<String> altNames) {
        variableDetectionRegExp = variableDetectionRegExpFactory(flags, altNames);
    }

    /**
     * Get the regular expression used to detect variables in the JSON string.
     */
    public static Pattern getVariableDetectionRegExp() {
        return variableDetectionRegExp;
    }

    public static String toObjectFieldPath(JsonAdapter<?, ?, ?> adapter, String key) {
        return validIdRegExp.matcher(key).matches() ? "." + key : "[" + adapter.toString(key) + "]";
    }
}
