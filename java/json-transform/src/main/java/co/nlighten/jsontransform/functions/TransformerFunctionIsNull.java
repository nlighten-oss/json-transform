package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

/*
 * For tests
 * @see TransformerFunctionIsNullTest
 */
public class TransformerFunctionIsNull extends TransformerFunction {
    public TransformerFunctionIsNull() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        var value = context.getJsonElement(null, true);
        return context.getAdapter().isNull(value);
    }
}
