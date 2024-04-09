package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;
import co.nlighten.jsontransform.JsonElementStreamer;

/*
 * For tests
 * @see TransformerFunctionReverseTest
 */
@Aliases("reverse")
@Documentation("Reverses the order of elements in an array")
@InputType(ArgType.Array)
@OutputType(ArgType.Array)
@TypeIsPiped
public class TransformerFunctionReverse<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionReverse(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    private int compare(JE a, JE b) {
        return -1;
    }

    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null) {
            return null;
        }
        return JsonElementStreamer.fromTransformedStream(context, streamer.stream().sorted(this::compare));
    }
}
