package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

import java.util.ArrayList;
import java.util.regex.Pattern;

/*
 * For tests
 * @see TransformerFunctionMatchTest
 */
@Aliases("matchall")
@Documentation("Returns all matches substring from input by a pattern (and optionally group id)")
@InputType(ArgType.Object)
@ArgumentType(value = "pattern", type = ArgType.String, position = 0, required = true,
              description = "Regular expression to match and extract from input string")
@ArgumentType(value = "group", type = ArgType.Integer, position = 1, defaultInteger = 0,
              description = "The group id to get")
@OutputType(ArgType.ArrayOfString)
public class TransformerFunctionMatchAll<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionMatchAll(JsonAdapter<JE, JA, JO> adapter) {
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
        var group = context.getInteger("group");
        var allMatches = new ArrayList<>();
        while (matcher.find()) {
            allMatches.add(matcher.group(group));
        }
        return allMatches.size() == 0 ? null : allMatches ;
    }
}
