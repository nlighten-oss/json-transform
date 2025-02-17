package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionLower extends TransformerFunction {
    public TransformerFunctionLower() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        var result = context.getString(null);
        return result == null ? null : result.toLowerCase();
    }
}
