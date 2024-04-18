package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * For tests
 * @see TransformerFunctionReduceTest
 */
@Aliases("reduce")
@Documentation("Reduce an array with an initial value (`identity`) and a context transformer to a single value")
@InputType(ArgType.Array)
@ArgumentType(value = "to", type = ArgType.Transformer, position = 0, defaultIsNull = true, required = true,
              description = "Transformer to apply on each element (with last accumulation) to get the next accumulation (inputs: ##accumulator, ##current, ##index)")
@ArgumentType(value = "identity", type = ArgType.Any, position = 1, defaultIsNull = true,
              description = "Initial value to start the accumulation with")
@OutputType(value = ArgType.Any, description = "Type of `to`'s result or `identity`'s")
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
