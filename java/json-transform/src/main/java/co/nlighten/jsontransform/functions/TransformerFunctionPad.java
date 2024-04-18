package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

/*
 * For tests
 * @see TransformerFunctionPadTest
 */
@Aliases("pad")
@Documentation(value = "Pad a provided string with a certain character repeated until a certain width of output string",
               notes = "(Strings longer than `width` will be returned as-is)")
@InputType(ArgType.Any)
@ArgumentType(value = "direction", type = ArgType.Enum, position = 0, defaultIsNull = true, required = true,
              enumValues = { "LEFT", "START", "RIGHT", "END" },
              description = "On which side of the input to pad")
@ArgumentType(value = "width", type = ArgType.Integer, position = 1, defaultIsNull = true, required = true,
              description = "What is the maximum length of the output string")
@ArgumentType(value = "pad_string", type = ArgType.String, position = 2, defaultString = "0",
              description = "The character(s) to pad with")
@OutputType(ArgType.String)
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
