package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionUpper extends TransformerFunction {
    public TransformerFunctionUpper() {
        super();
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var result = context.getString(null);
        return result == null ? null : result.toUpperCase();
    }
}
