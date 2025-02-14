package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class TransformerFunctionReduce extends TransformerFunction {
    public TransformerFunctionReduce() {
        super(FunctionDescription.of(
                Map.of(
                        "to", ArgumentType.of(ArgType.Transformer).position(0).defaultIsNull(true),
                        "identity", ArgumentType.of(ArgType.Any).position(1).defaultIsNull(true)
                )
        ));
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null)
            return null;
        var identity = context.getJsonElement("identity");
        var to = context.getJsonElement("to", false);

        var i = new AtomicInteger(0);
        return ((Stream<Object>)streamer.stream())
                .reduce(identity, (acc, x) -> context.transformItem(to, x, i.getAndIncrement(), "##accumulator", acc));
    }
}
