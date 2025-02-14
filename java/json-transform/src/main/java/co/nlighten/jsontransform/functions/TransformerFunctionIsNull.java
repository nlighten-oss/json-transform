package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionIsNull extends TransformerFunction {
    public TransformerFunctionIsNull() {
        super();
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var value = context.getJsonElement(null, true);
        return context.getAdapter().isNull(value);
    }
}
