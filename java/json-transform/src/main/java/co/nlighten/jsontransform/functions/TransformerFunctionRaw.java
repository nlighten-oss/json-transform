package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionRaw extends TransformerFunction {
    public TransformerFunctionRaw() {
        super();
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        return context.getJsonElement(null, false);
    }
}
