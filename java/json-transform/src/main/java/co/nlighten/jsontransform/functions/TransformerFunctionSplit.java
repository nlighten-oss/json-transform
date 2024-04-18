package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

/*
 * For tests
 * @see TransformerFunctionSplitTest
 */
@Aliases("split")
@Documentation("Splits a string using a given delimiter/regex")
@InputType(ArgType.String)
@ArgumentType(value = "delimiter", type = ArgType.String, position = 0, defaultString = "",
              description = "Delimiter to split by (can be a regular expression)")
@ArgumentType(value = "limit", type = ArgType.Integer, position = 1, defaultInteger = 0,
              description = "Limit the amount of elements returned (and by that, the amount the pattern get matched)")
@OutputType(value = ArgType.ArrayOfString)
public class TransformerFunctionSplit<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionSplit(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        return str.split(context.getString("delimiter"), context.getInteger("limit"));
    }
}
