package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

/*
 * For tests
 * @see TransformerFunctionXorTest
 */
public class TransformerFunctionXor<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionXor(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var value = context.getJsonElementStreamer(null);
        return value.stream().filter(adapter::isTruthy).count() == 1;
    }
}
