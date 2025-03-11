package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;
import co.nlighten.jsontransform.JsonElementStreamer;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TransformerFunctionFilter extends TransformerFunction {
    public TransformerFunctionFilter() {
        super(FunctionDescription.of(
            Map.of(
            "by", ArgumentType.of(ArgType.Transformer).position(0)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null)
            return null;
        var by = context.getJsonElement("by", false);
        var index = new AtomicInteger(0);
        var adapter = context.getAdapter();
        return JsonElementStreamer.fromTransformedStream(context, streamer.stream()
                .filter(item -> {
                    var condition = context.transformItem(by, item, index.getAndIncrement());
                    return adapter.isTruthy(condition);
                }));
    }
}
