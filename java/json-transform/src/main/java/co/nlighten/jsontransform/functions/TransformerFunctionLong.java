package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionLong extends TransformerFunction {
    public TransformerFunctionLong() {
        super();
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        return context.getLong(null);
    }
}
