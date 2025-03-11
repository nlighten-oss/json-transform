package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class TransformerFunctionAvg extends TransformerFunction {

    public TransformerFunctionAvg() {
        super(FunctionDescription.of(
            Map.of(
            "default", ArgumentType.of(ArgType.BigDecimal).position(0).defaultBigDecimal(BigDecimal.ZERO),
            "by", ArgumentType.of(ArgType.Transformer).position(1).defaultIsNull(true))
        ));
    }

    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null || streamer.knownAsEmpty())
            return null;
        var hasBy = context.has("by");
        var by = context.getJsonElement( "by", false);
        var _default = Objects.requireNonNullElse(context.getBigDecimal("default"), BigDecimal.ZERO);
        var size = new AtomicInteger(0);
        var identity = BigDecimal.valueOf(0, FunctionHelpers.MAX_SCALE);
        var adapter = context.getAdapter();
        var result = streamer.stream()
                .map(t -> {
                    size.getAndIncrement();
                    var res = hasBy ? context.transformItem(by, t) : t;
                    return adapter.isNull(res) ? _default : adapter.getNumberAsBigDecimal(res);
                })
                .reduce(identity, BigDecimal::add)
                .divide(BigDecimal.valueOf(size.get()), FunctionHelpers.MAX_SCALE_ROUNDING);
        return result;
    }
}
