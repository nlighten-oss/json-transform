package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * For tests
 * @see TransformerFunctionFindTest
 */
@Aliases("find")
@Documentation("Find the first element in a specified array that satisfy the predicate transformer")
@InputType(ArgType.Array)
@ArgumentType(value = "by", type = ArgType.Transformer, position = 0, required = true,
              description = "A predicate transformer for an element (##current to refer to the current item and ##index to its index)")
@OutputType(value = ArgType.Any, description = "Same as found value")
public class TransformerFunctionFind<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionFind(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null)
            return null;
        var by = context.getJsonElement("by", false);
        var index = new AtomicInteger(0);
        return streamer.stream()
                .filter(item -> {
                    var condition = context.transformItem(by, item, index.getAndIncrement());
                    return adapter.isTruthy(condition);
                })
                .findFirst()
                .orElse(null);
    }
}
