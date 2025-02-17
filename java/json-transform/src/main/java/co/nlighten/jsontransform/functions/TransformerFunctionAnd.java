package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

public class TransformerFunctionAnd extends TransformerFunction {
    public TransformerFunctionAnd() {
        super();
    }

    @Override
    public Object apply(FunctionContext context) {
        var adapter = context.getAdapter();
        var value = context.getJsonElementStreamer(null);
        if (value == null) {
            return false;
        }
        return value.stream().allMatch(adapter::isTruthy);
    }
}
