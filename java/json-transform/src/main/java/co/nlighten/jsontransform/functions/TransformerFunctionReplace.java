package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionReplace extends TransformerFunction {
    public TransformerFunctionReplace() {
        super(FunctionDescription.of(
                Map.of(
                        "find", ArgumentType.of(ArgType.String).position(0).defaultString(""),
                        "replacement", ArgumentType.of(ArgType.String).position(1).defaultString(""),
                        "type", ArgumentType.of(ArgType.Enum).position(2).defaultEnum("STRING"),
                        "from", ArgumentType.of(ArgType.Integer).position(3).defaultInteger(0)
                )
        ));
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
    public Object apply(FunctionContext context) {
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
