package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * For tests
 * @see TransformerFunctionReduceTest
 */
@ArgumentType(value = "to", type = ArgType.Transformer, position = 0, defaultIsNull = true)
@ArgumentType(value = "identity", type = ArgType.Any, position = 1, defaultIsNull = true)
public class TransformerFunctionReduce<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionReduce(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null)
            return null;
        var identity = context.getJsonElement("identity");
        var to = context.getJsonElement("to", false);

        var i = new AtomicInteger(0);
        return streamer.stream()
                .reduce(identity, (acc, x) -> context.transformItem(to, x, i.getAndIncrement(), "##accumulator", acc));
    }
}
