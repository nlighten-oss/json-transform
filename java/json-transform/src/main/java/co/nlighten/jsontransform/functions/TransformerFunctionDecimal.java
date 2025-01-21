package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.FunctionHelpers;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

import java.math.RoundingMode;

/*
 * For tests
 * @see TransformerFunctionDecimalTest
 */
@ArgumentType(value = "scale", type = ArgType.Integer, position = 0, defaultInteger = -1)
@ArgumentType(value = "rounding", type = ArgType.Enum, position = 1, defaultEnum = "HALF_UP")
public class TransformerFunctionDecimal<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionDecimal(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var result = context.getBigDecimal(null);
        if (result == null) return null;
        var scale = context.getInteger("scale");
        var roundingMode = context.getEnum("rounding");
        if (scale == FunctionHelpers.NO_SCALE && result.scale() > FunctionHelpers.MAX_SCALE) {
            scale = FunctionHelpers.MAX_SCALE;
        }
        if (scale > -1) {
            result = result.setScale(scale, RoundingMode.valueOf(roundingMode));
        }
        return result;
    }
}
