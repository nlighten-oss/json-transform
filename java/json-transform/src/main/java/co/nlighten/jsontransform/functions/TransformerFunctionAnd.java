package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

import java.util.concurrent.CompletionStage;

public class TransformerFunctionAnd extends TransformerFunction {
    public TransformerFunctionAnd() {
        super();
    }

    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var adapter = context.getAdapter();
        return context.getJsonElementStreamer(null).thenApply(value -> {
            if (value == null) {
                return false;
            }
            return value.stream().allMatch(adapter::isTruthy);
        });
    }
}
