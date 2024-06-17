package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class JsonTransformerUtils {

    private static Pattern variableDetectionRegExp = variableDetectionRegExpFactory(null, null);

    public static Pattern variableDetectionRegExpFactory(Integer flags, List<String> altNames) {
        var altPrefixes =  altNames != null && !altNames.isEmpty()
                ? "(" + String.join("|", altNames) + ")?"
                : "";
        return Pattern.compile(
                "(?<![\\])}?!@#$%^&*+\\\\\\w])(#[a-z_]+[a-z_\\d]*|\\$(?!\\$)" + altPrefixes + ")(((\\.(?![-\\w$]+\\()[-\\w$]+)|(\\[[^\\]\\n]+]))+|(?=[^\\w.]|$))",
                flags != null ? flags : 0);
    }

    private static <JE, JA extends Iterable<JE>, JO extends JE> void findAllVariableUses(JsonAdapter<JE, JA, JO> adapter, JE element, Map<String, Object> result, String path) {
        if (adapter.isJsonString(element)) {
            var string = adapter.getAsString(element);
            var matcher = variableDetectionRegExp.matcher(string);
            while (matcher.find()) {
                var v = matcher.group();
                if (!result.containsKey(v)) result.put(v, path);
            }
        } else if (adapter.jArray.is(element)) {
            var array = adapter.jArray.type.cast(element);
            var size = adapter.jArray.size(array);
            for (var i = 0; i < size; i++) {
                findAllVariableUses(adapter, adapter.jArray.get(array, i), result, path + "[" + i + "]");
            }
        } else if (adapter.jObject.is(element)) {
            var coll = adapter.jObject.type.cast(element);
            var isObjectFunction = adapter.jObject.keySet(coll).stream().anyMatch(x -> x.startsWith("$$"));
            adapter.jObject.keySet(coll).forEach(x -> {
                var value = adapter.jObject.get(coll, x);
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
     * @param <JE> the JSON element type
     * @param <JA> the JSON array type
     * @param <JO> the JSON object type
     */
    public static <JE, JA extends Iterable<JE>, JO extends JE> Map<String, Object> findAllVariableUses(JsonAdapter<JE, JA, JO> adapter, JE element) {
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
}
