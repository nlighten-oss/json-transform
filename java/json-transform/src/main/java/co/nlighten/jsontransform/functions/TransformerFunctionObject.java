package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

/*
 * For tests
 * @see TransformerFunctionObjectTest
 */
public class TransformerFunctionObject extends TransformerFunction {
    public TransformerFunctionObject() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        var adapter = context.getAdapter();
        var result = adapter.createObject();
        if (streamer != null) {
            streamer.stream().forEach(entry -> {
                if (adapter.isJsonArray(entry)) {
                    var size = adapter.size(entry);
                    if (size > 1) {
                        var key = adapter.get(entry, 0);
                        if (!adapter.isNull(key)) {
                            adapter.add(result, context.getAsString(key), adapter.get(entry, 1));
                        }
                    }
                }
            });
        }
        return result;
    }
}
