package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.JsonElementStreamer;
import co.nlighten.jsontransform.functions.common.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class TransformerFunctionRange extends TransformerFunction {
    public TransformerFunctionRange() {
        super(FunctionDescription.of(
            Map.of(
            "start", ArgumentType.of(ArgType.BigDecimal).position(0),
            "end", ArgumentType.of(ArgType.BigDecimal).position(1),
            "step", ArgumentType.of(ArgType.BigDecimal).position(2).defaultBigDecimal(BigDecimal.ONE)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        BigDecimal start;
        BigDecimal end;
        BigDecimal step;
        if (context.has("start")) {
            start = context.getBigDecimal("start");
            end = context.getBigDecimal("end");
            step = context.getBigDecimal("step");
        } else {
            var arr = context.getJsonArray(null);
            var adapter = context.getAdapter();
            var length = adapter.size(arr);
            start = length > 0 ? adapter.getNumberAsBigDecimal(adapter.get(arr, 0)) : null;
            end = length > 1 ? adapter.getNumberAsBigDecimal(adapter.get(arr, 1)) : null;
            step = length > 2 ? adapter.getNumberAsBigDecimal(adapter.get(arr, 2)) : BigDecimal.ONE;
        }
        // sanity check
        if (start == null ||
            end == null ||
            (end.compareTo(start) < 0 && step.signum() > 0) ||
            (end.compareTo(start) > 0 && step.signum() < 0)
        ) {
            return null;
        }
        var size = end.subtract(start).divideToIntegralValue(step).add(BigDecimal.ONE).intValue();
        var value = new AtomicReference<>(start);
        return JsonElementStreamer.fromTransformedStream(context,
                Stream.generate(() -> value.getAndUpdate(next -> next.add(step))).limit(size)
        );
    }
}
