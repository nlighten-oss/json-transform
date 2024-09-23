package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

/*
 * For tests
 * @see TransformerFunctionWrapTest
 */
@ArgumentType(value = "prefix", type = ArgType.String, position = 0, defaultString = "")
@ArgumentType(value = "suffix", type = ArgType.String, position = 1, defaultString = "")
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
