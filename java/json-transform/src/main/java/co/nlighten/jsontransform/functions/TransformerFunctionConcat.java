package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.JsonElementStreamer;

import java.util.stream.Stream;

public class TransformerFunctionConcat extends TransformerFunction {
    public TransformerFunctionConcat() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null) return null;

        var adapter = context.getAdapter();
        return JsonElementStreamer.fromTransformedStream(context, streamer.stream()
            .flatMap(itm -> {
                if (adapter.isNull(itm)) {
                    return Stream.empty();
                } else if (adapter.isJsonArray(itm)) {
                    return adapter.stream(itm);
                }
                return Stream.of(itm);
            }));
    }
}
