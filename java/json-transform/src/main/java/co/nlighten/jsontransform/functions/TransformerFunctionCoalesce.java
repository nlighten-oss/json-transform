package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

import java.util.concurrent.CompletionStage;

public class TransformerFunctionCoalesce extends TransformerFunction {

    public TransformerFunctionCoalesce() {
        super();
    }

    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        return context.getJsonElementStreamer(null).thenApply(streamer -> {
            if (streamer == null || streamer.knownAsEmpty()) return null;
            var adapter = context.getAdapter();
            return streamer.stream()
                    .filter(itm -> !adapter.isNull(itm))
                    .findFirst()
                    .orElse(null);
        });
    }
}
