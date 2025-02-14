package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TransformerFunctionFind extends TransformerFunction {
    public TransformerFunctionFind() {
        super(FunctionDescription.of(
            Map.of(
            "by", ArgumentType.of(ArgType.Transformer).position(0)
            )));
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null)
            return null;
        var by = context.getJsonElement("by", false);
        var index = new AtomicInteger(0);
        var adapter = context.getAdapter();
        return streamer.stream()
                .filter(item -> {
                    var condition = context.transformItem(by, item, index.getAndIncrement());
                    return adapter.isTruthy(condition);
                })
                .findFirst()
                .orElse(null);
    }
}
