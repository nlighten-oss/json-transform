package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionCoalesce extends TransformerFunction {

    public TransformerFunctionCoalesce() {
        super();
    }

    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null || streamer.knownAsEmpty()) return null;
        var adapter = context.getAdapter();
        return streamer.stream()
                .filter(itm -> !adapter.isNull(itm))
                .findFirst()
                .orElse(null);
    }
}
