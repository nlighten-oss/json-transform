package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

/*
 * For tests
 * @see TransformerFunctionStringTest
 */
@ArgumentType(value = "json", type = ArgType.Boolean, position = 0, defaultBoolean = false)
public class TransformerFunctionString<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionString(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var value = context.getUnwrapped(null);
        if (context.getBoolean("json")) {
            // although gson.toJson will return "null" eventually, this is quicker
            if (value == null) {
                return "null";
            }
            return adapter.toString(value);
        }
        return context.getAsString(value);
    }
}
