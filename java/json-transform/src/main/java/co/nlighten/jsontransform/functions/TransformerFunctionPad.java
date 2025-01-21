package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

/*
 * For tests
 * @see TransformerFunctionPadTest
 */
@ArgumentType(value = "direction", type = ArgType.Enum, position = 0, defaultIsNull = true)
@ArgumentType(value = "width", type = ArgType.Integer, position = 1, defaultIsNull = true)
@ArgumentType(value = "pad_string", type = ArgType.String, position = 2, defaultString = "0")
public class TransformerFunctionPad<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionPad(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var res = context.getString(null);
        if (res == null)
            return null;

        var direction = context.getEnum("direction");
        var width = context.getInteger("width");
        if (direction == null || width == null || res.length() >= width) {
            return res;
        }

        var paddingSize = width - res.length();
        var padding = context.getString("pad_string").repeat(paddingSize);
        if (padding.length() > paddingSize) { // in case padding string is more than one character
            padding = padding.substring(0, paddingSize);
        }
        StringBuilder sb = new StringBuilder();
        if ("LEFT".equalsIgnoreCase(direction) || "START".equalsIgnoreCase(direction)) {
            sb.append(padding).append(res);
        } else if ("RIGHT".equalsIgnoreCase(direction) || "END".equalsIgnoreCase(direction)) {
            sb.append(res).append(padding);
        }
        return sb.toString();
    }
}
