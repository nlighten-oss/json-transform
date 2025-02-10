package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

/*
 * For tests
 * @see TransformerFunctionEvalTest
 */
public class TransformerFunctionEval extends TransformerFunction {

    public TransformerFunctionEval() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        var eval = context.getJsonElement(null,true);
        return context.transform(context.getPath() + "/.", eval, true);
    }
}
