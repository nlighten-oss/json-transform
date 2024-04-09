package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

import java.util.regex.Pattern;

/*
 * For tests
 * @see TransformerFunctionMatchTest
 */
@Aliases("match")
@Documentation("Returns a matched substring from input by a pattern (and optionally group id)")
@InputType(ArgType.Object)
@ArgumentType(value = "pattern", type = ArgType.String, position = 0, required = true,
              description = "Regular expression to match and extract from input string")
@ArgumentType(value = "group", type = ArgType.Integer, position = 1, defaultInteger = 0,
              description = "The group id to get")
@OutputType(ArgType.String)
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
