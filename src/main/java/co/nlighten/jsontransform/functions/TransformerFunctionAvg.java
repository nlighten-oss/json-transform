package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.FunctionHelpers;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Aliases("avg")
@Documentation("Returns the average of all values in the array")
@InputType(ArgType.Array)

@ArgumentType(value = "default", type = ArgType.BigDecimal, position = 0, defaultBigDecimal = 0,
              description = "The default value to use for empty values")
@ArgumentType(value = "by", type = ArgType.Transformer, position = 1, required = false,
              defaultString = "##current",
              description = "A transformer to extract a property to sum by (using ##current to refer to the current item)")

@OutputType(ArgType.BigDecimal)
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
        var def = Objects.requireNonNullElse(context.getBigDecimal("default"), BigDecimal.ZERO);
        var size = new AtomicInteger(0);
        var result = streamer.stream()
                .map(t -> {
                    size.getAndIncrement();
                    var res = !adapter.isNull(by) ? context.transformItem(by, t) : t;
                    return adapter.isNull(res) ? def : adapter.getNumberAsBigDecimal(res);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(size.get()), RoundingMode.HALF_UP);

        // cap scale at max
        if (result.scale() > FunctionHelpers.MAX_SCALE) {
            result = result.setScale(FunctionHelpers.MAX_SCALE, FunctionHelpers.MAX_SCALE_ROUNDING);
        }
        return result;
    }
}
