package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionEval extends TransformerFunction {

    public TransformerFunctionEval() {
        super();
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var eval = context.getJsonElement(null,true);
        return context.transform(context.getPath() + "/.", eval, true);
    }
}
