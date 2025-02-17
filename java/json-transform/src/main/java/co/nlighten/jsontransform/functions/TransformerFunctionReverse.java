package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.JsonElementStreamer;

public class TransformerFunctionReverse extends TransformerFunction {
    public TransformerFunctionReverse() {
        super();
    }
    private  <T> int compare(T a, T b) {
        return -1;
    }

    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null) {
            return null;
        }
        return JsonElementStreamer.fromTransformedStream(context, streamer.stream().sorted(this::compare));
    }
}
