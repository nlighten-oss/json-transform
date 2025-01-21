package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

import java.util.regex.Pattern;

/*
 * For tests
 * @see TransformerFunctionMatchTest
 */
@ArgumentType(value = "pattern", type = ArgType.String, position = 0)
@ArgumentType(value = "group", type = ArgType.Integer, position = 1, defaultInteger = 0)
public class TransformerFunctionMatch<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionMatch(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
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
