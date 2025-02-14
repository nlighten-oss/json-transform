package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionOr extends TransformerFunction {
    public TransformerFunctionOr() {
        super();
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var value = context.getJsonElementStreamer(null);
        var adapter = context.getAdapter();
        return value.stream().anyMatch(adapter::isTruthy);
    }
}
