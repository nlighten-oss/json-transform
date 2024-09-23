package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.FunctionHelpers;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@ArgumentType(value = "default", type = ArgType.BigDecimal, position = 0, defaultBigDecimal = 0)
@ArgumentType(value = "by", type = ArgType.Transformer, position = 1, defaultString = "##current")
public class TransformerFunctionAvg<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {

    public TransformerFunctionAvg(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }

    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null || streamer.knownAsEmpty())
            return null;
        var by = context.getJsonElement( "by", false);
        var _default = Objects.requireNonNullElse(context.getBigDecimal("default"), BigDecimal.ZERO);
        var size = new AtomicInteger(0);
        var identity = BigDecimal.valueOf(0, FunctionHelpers.MAX_SCALE);
        var result = streamer.stream()
                .map(t -> {
                    size.getAndIncrement();
                    var res = !adapter.isNull(by) ? context.transformItem(by, t) : t;
                    return adapter.isNull(res) ? _default : adapter.getNumberAsBigDecimal(res);
                })
                .reduce(identity, BigDecimal::add)
                .divide(BigDecimal.valueOf(size.get()), FunctionHelpers.MAX_SCALE_ROUNDING);
        return result;
    }
}
