package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.FunctionHelpers;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

import java.math.BigDecimal;
import java.util.Objects;

@ArgumentType(value = "default", type = ArgType.BigDecimal, position = 0, defaultBigDecimal = 0)
@ArgumentType(value = "by", type = ArgType.Transformer, position = 1, defaultIsNull = true)
public class TransformerFunctionSum<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {

    public TransformerFunctionSum(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null || streamer.knownAsEmpty())
            return null;
        var by = context.getJsonElement("by", false);
        var def = Objects.requireNonNullElse(context.getBigDecimal("default"), BigDecimal.ZERO);
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
