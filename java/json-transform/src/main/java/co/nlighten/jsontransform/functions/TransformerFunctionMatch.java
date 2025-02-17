package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;
import java.util.regex.Pattern;

public class TransformerFunctionMatch extends TransformerFunction {
    public TransformerFunctionMatch() {
        super(FunctionDescription.of(
            Map.of(
            "pattern", ArgumentType.of(ArgType.String).position(0),
            "group", ArgumentType.of(ArgType.Integer).position(1).defaultInteger(0)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        var patternString = context.getString("pattern");
        if (patternString == null) {
            return null;
        }
        var matcher = Pattern.compile(patternString).matcher(str);
        if (!matcher.find())
            return null; // not found
        var group = context.getInteger("group");
        return matcher.group(group);
    }
}
