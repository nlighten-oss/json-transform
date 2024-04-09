package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

/*
 * For tests
 * @see TransformerFunctionContainsTest
 */
@Aliases("contains")
@Documentation("Checks whether an array contains a certain value")
@InputType(ArgType.Array)
@ArgumentType(value = "that", type = ArgType.Any, position = 0, defaultIsNull = true,
              description = "The value to look for")
@OutputType(ArgType.Boolean)
public class TransformerFunctionContains<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionContains(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null) return null;
        var that = context.getJsonElement( "that");
        return streamer.stream().anyMatch(el -> el.equals(that));
    }
}
