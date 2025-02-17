package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.JsonElementStreamer;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

import java.util.concurrent.atomic.AtomicInteger;

public class TransformerFunctionEntries extends TransformerFunction {
    public TransformerFunctionEntries() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        var input = context.getJsonElement(null);
        var adapter = context.getAdapter();
        if (adapter.isJsonArray(input)) {
            var i = new AtomicInteger(0);
            return JsonElementStreamer.fromTransformedStream(context, adapter.stream(input)
                    .map(a -> {
                        var entry = adapter.createArray(2);
                        adapter.add(entry, i.getAndIncrement());
                        adapter.add(entry, a);
                        return entry;
                    }));
        }
        if (adapter.isJsonObject(input)) {
            return JsonElementStreamer.fromTransformedStream(context, adapter.entrySet(input)
                    .stream()
                    .map(e -> {
                        var entry = adapter.createArray(2);
                        adapter.add(entry, e.getKey());
                        adapter.add(entry, e.getValue());
                        return entry;
                    }));
        }
        return null;
    }
}
