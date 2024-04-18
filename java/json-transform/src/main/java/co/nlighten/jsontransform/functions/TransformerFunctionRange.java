package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/*
 * For tests
 * @see TransformerFunctionRangeTest
 */
@Aliases("range")
@Documentation("Creates an array with a sequence of numbers starting with `start` up-to `end` in steps of `step`")
@InputType(ArgType.Any)
@ArgumentType(value = "start", type = ArgType.BigDecimal, position = 0, defaultIsNull = true, required = true,
              description = "First value")
@ArgumentType(value = "end", type = ArgType.BigDecimal, position = 1, defaultIsNull = true, required = true,
              description = "Max value to appear in sequence")
@ArgumentType(value = "step", type = ArgType.BigDecimal, position = 2, defaultBigDecimal = 1d,
              description = "Step to add on each iteration to the previous value in the sequence")
@OutputType(ArgType.ArrayOfBigDecimal)
public class TransformerFunctionRange<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionRange(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var start = context.getBigDecimal("start");
        var end = context.getBigDecimal("end");
        if (start == null || end == null) {
            return new BigDecimal[0];
        }
        var step = context.getBigDecimal("step");
        var result = new BigDecimal[end.subtract(start).divide(step, RoundingMode.FLOOR).add(BigDecimal.ONE).intValue()];
        var index = 0;
        for (var l = start; l.compareTo(end) <= 0; l = l.add(step)) {
            result[index] = l;
            index++;
        }
        return result;
    }
}
