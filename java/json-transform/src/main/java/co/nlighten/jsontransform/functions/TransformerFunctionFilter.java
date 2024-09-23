package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.JsonElementStreamer;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * For tests
 * @see TransformerFunctionFilterTest
 */
@ArgumentType(value = "by", type = ArgType.Transformer, position = 0, defaultIsNull = true)
public class TransformerFunctionFilter<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionFilter(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null)
            return null;
        var by = context.getJsonElement("by", false);
        var index = new AtomicInteger(0);
        return JsonElementStreamer.fromTransformedStream(context, streamer.stream()
                .filter(item -> {
                    var condition = context.transformItem(by, item, index.getAndIncrement());
                    return adapter.isTruthy(condition);
                }));
    }
}
