package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

/*
 * For tests
 * @see TransformerFunctionReplaceTest
 */
@Aliases("replace")
@Documentation("Search and replaces a substring in the given input")
@InputType(ArgType.String)
@ArgumentType(value = "find", type = ArgType.String, position = 0, defaultString = "", required = true,
              description = "Value to search in input string (depends on `type`, if set to `REGEX`, should be a regular expression)")
@ArgumentType(value = "replacement", type = ArgType.String, position = 1, defaultString = "",
              description = "Value to replace each match (or only first if `type = REGEX-FIRST`), when using regular expression can use group matches (e.g. `$1`) (Note: to escape `$` if starting with it)")
@ArgumentType(value = "type", type = ArgType.Enum, position = 2, defaultEnum = "STRING",
              enumValues = {"STRING", "FIRST", "REGEX", "REGEX-FIRST"},
              description = "Matching type")
@ArgumentType(value = "from", type = ArgType.Integer, position = 3, defaultInteger = 0,
              description = "At what index in the string the search should start from")
@OutputType(ArgType.String)
public class TransformerFunctionReplace<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionReplace(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    private static String replaceOnce(String value, String find, String replacement, Integer fromIndex) {
        int index = value.indexOf(find, fromIndex);
        if (index == -1) {
            return value;
        }
        return value.substring(0, index)
                .concat(replacement)
                .concat(value.substring(index + find.length()));
    }

    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        var find = context.getString("find");
        if (find == null) {
            return str;
        }
        var replacement = context.getString("replacement");
        var from = context.getInteger("from");
        var validFrom = from > 0 && str.length() > from;
        return switch (context.getEnum("type")) {
            case "FIRST" -> replaceOnce(str, find, replacement, validFrom ? from : 0);
            case "REGEX" -> validFrom
                            ? str.substring(0, from) + str.substring(from).replaceAll(find, replacement)
                            : str.replaceAll(find, replacement);
            case "REGEX-FIRST" -> validFrom
                                  ? str.substring(0, from) + str.substring(from).replaceFirst(find, replacement)
                                  : str.replaceFirst(find, replacement);
            default -> validFrom
                       ? str.substring(0, from) + str.substring(from).replace(find, replacement)
                       : str.replace(find, replacement);
        };
    }
}
