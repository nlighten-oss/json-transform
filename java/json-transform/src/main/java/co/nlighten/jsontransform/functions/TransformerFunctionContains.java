package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionContains extends TransformerFunction {
    public TransformerFunctionContains() {
        super(FunctionDescription.of(
            Map.of(
            "that", ArgumentType.of(ArgType.Any).position(0).defaultIsNull(true)
            )
        ));
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null) return null;
        var that = context.getJsonElement( "that");
        return streamer.stream().anyMatch(el -> el.equals(that));
    }
}
