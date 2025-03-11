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
            "start", ArgumentType.of(ArgType.BigDecimal).position(0).defaultIsNull(true),
            "end", ArgumentType.of(ArgType.BigDecimal).position(1).defaultIsNull(true),
            "step", ArgumentType.of(ArgType.BigDecimal).position(2).defaultBigDecimal(BigDecimal.ONE)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var start = context.getBigDecimal("start");
        var end = context.getBigDecimal("end");
        if (start == null || end == null) {
            return new BigDecimal[0];
        }
        var step = context.getBigDecimal("step");
        var size = end.subtract(start).divideToIntegralValue(step).add(BigDecimal.ONE).intValue();

        var value = new AtomicReference<>(start);
        return JsonElementStreamer.fromTransformedStream(context,
                Stream.generate(() -> value.getAndUpdate(next -> next.add(step))).limit(size)
        );
    }
}
