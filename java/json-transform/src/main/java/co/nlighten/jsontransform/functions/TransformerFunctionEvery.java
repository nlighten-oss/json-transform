package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionEvery extends TransformerFunction {

    public TransformerFunctionEvery() {
        super(FunctionDescription.of(
            Map.of(
                "by", ArgumentType.of(ArgType.Transformer).position(0)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var adapter = context.getAdapter();
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null) {
            return false;
        }
        var by = context.getJsonElement("by", false);
        return streamer.stream()
                .map(x -> context.transformItem(by, x))
                .allMatch(adapter::isTruthy);
    }
}
