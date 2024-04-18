package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

/*
 * For tests
 * @see TransformerFunctionWrapTest
 */
@Aliases("wrap")
@Documentation("Wrap an input string with `prefix` and `suffix`")
@InputType(ArgType.String)
@ArgumentType(value = "prefix", type = ArgType.String, position = 0, defaultString = "",
              description = "String that will prefix the input in the output")
@ArgumentType(value = "suffix", type = ArgType.String, position = 1, defaultString = "",
              description = "String that will suffix the input in the output")
@OutputType(ArgType.String)
public class TransformerFunctionWrap<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionWrap(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var res = context.getString(null);
        if (res == null)
            return null;
        return context.getString("prefix") + res + context.getString("suffix");
    }
}
