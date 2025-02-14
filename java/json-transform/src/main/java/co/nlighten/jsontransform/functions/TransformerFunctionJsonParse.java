package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionJsonParse extends TransformerFunction {
    public TransformerFunctionJsonParse() {
        super();
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        return context.getAdapter().parse(str);
    }
}
