package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

/*
 * For tests
 * @see TransformerFunctionStringTest
 */
@Aliases("string")
@Documentation("Converts to string (if `json` set to `true`, will convert null and strings also as JSON strings)")
@InputType(ArgType.Any)
@ArgumentType(value = "json", type = ArgType.Boolean, position = 0, defaultBoolean = false,
              description = "Whether to convert `null` and strings to json (otherwise, null stays null and strings are returned as-is)")
@OutputType(ArgType.String)
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
