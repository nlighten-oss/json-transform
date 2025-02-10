package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public class TransformerFunctionSum extends TransformerFunction {

    public TransformerFunctionSum() {
        super(FunctionDescription.of(
            Map.of(
            "default", ArgumentType.of(ArgType.BigDecimal).position(0).defaultBigDecimal(BigDecimal.ZERO),
            "by", ArgumentType.of(ArgType.Transformer).position(1).defaultIsNull(true)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null || streamer.knownAsEmpty())
            return null;
        var by = context.getJsonElement("by", false);
        var def = Objects.requireNonNullElse(context.getBigDecimal("default"), BigDecimal.ZERO);
        var adapter = context.getAdapter();
        var result = streamer.stream()
                .map(t -> {
                    var res = !adapter.isNull(by) ? context.transformItem(by, t) : t;
                    return adapter.isNull(res) ? def : adapter.getNumberAsBigDecimal(res);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // cap scale at max
        if (result.scale() > FunctionHelpers.MAX_SCALE) {
            result = result.setScale(FunctionHelpers.MAX_SCALE, FunctionHelpers.MAX_SCALE_ROUNDING);
        }
        return result;
    }
}
