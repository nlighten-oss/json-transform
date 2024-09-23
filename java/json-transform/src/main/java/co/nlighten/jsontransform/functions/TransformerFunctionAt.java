package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

/*
 * For tests
 * @see TransformerFunctionAtTest
 */
@ArgumentType(value = "index", type = ArgType.Integer, position = 0, defaultIsNull = true)
public class TransformerFunctionAt<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionAt(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }

    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var value = context.getJsonElementStreamer(null);
        var index = context.getInteger("index");
        if (index == null) {
            return null;
        }
        if (index == 0) {
            return value.stream().findFirst().orElse(null);
        }
        if (index > 0) {
            return value.stream(index.longValue(), null).findFirst().orElse(null);
        }
        // negative
        var arr = value.toJsonArray();
        return jArray.get(arr, jArray.size(arr) + index);
    }
}
