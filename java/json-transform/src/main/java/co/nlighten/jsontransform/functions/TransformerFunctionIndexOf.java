package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TransformerFunctionIndexOf extends TransformerFunction {
    public TransformerFunctionIndexOf() {
        super(FunctionDescription.of(
            Map.of(
            "of", ArgumentType.of(ArgType.Any).position(0)
            )));
    }
    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null)
            return null;
        var of = context.getJsonElement("of");
        var index = new AtomicInteger(0);
        var adapter = context.getAdapter();
        return streamer.stream()
                .sequential()
                .peek(item -> index.incrementAndGet())
                .anyMatch(item -> adapter.areEqual(item, of)) ? index.get() - 1 : -1;
    }
}
