package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.JsonElementStreamer;

/*
 * For tests
 *
 * @see TransformerFunctionValueTest
 */
public class TransformerFunctionValue extends TransformerFunction {
    public TransformerFunctionValue() {
        super();
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var result = context.get(null, true);
        if (result instanceof JsonElementStreamer streamer) {
            result = streamer.toJsonArray();
        }
        if (context.getAdapter().isTruthy(result)) {
            return result;
        }
        return null;
    }
}
