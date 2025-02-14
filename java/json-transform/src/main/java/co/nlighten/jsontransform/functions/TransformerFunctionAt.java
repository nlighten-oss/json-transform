package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;
import java.util.concurrent.CompletionStage;

public class TransformerFunctionAt extends TransformerFunction {
    public TransformerFunctionAt() {
        super(FunctionDescription.of(
            Map.of("index", ArgumentType.of(ArgType.Integer).position(0).defaultIsNull(true))
        ));
    }

    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        return context.getJsonElementStreamer(null).thenApply(streamer ->
            context.getInteger("index").thenApply(index -> {
                if (index == null) {
                    return null;
                }
                if (index == 0) {
                    return streamer.stream().findFirst().orElse(null);
                }
                if (index > 0) {
                    return streamer.stream(index.longValue(), null).findFirst().orElse(null);
                }
                // negative
                var arr = streamer.toJsonArray();
                var adapter = context.getAdapter();
                return adapter.get(arr, adapter.size(arr) + index);
            }));
    }
}
