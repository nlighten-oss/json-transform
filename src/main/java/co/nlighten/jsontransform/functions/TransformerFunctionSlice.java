package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;
import co.nlighten.jsontransform.JsonElementStreamer;

/*
 * For tests
 * @see TransformerFunctionSliceTest
 */
@Aliases("slice")
@Documentation("Gets a slice of an array by indices (negative begin index will slice from the end)")
@InputType(value = ArgType.Array, description = "Array to fetch from")
@ArgumentType(value = "begin", type = ArgType.Integer, position = 0, defaultInteger = 0, required = true,
              description = "Index of element to start slice from (if negative, counts from the end of the array)")
@ArgumentType(value = "end", type = ArgType.Integer, position = 1, defaultIsNull = true,
              description = "Index of last element to slice to (if negative, counts from the end of the array)")
@OutputType(value = ArgType.Any, description = "Element at index, or null if undefined")
public class TransformerFunctionSlice<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionSlice(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var value = context.getJsonElementStreamer(null);
        var begin = context.getInteger("begin");
        var end = context.getInteger("end");
        if (begin >= 0) {
            if (end == null) {
                // // slice() / slice(1)
                return begin == 0 ? value : JsonElementStreamer.fromTransformedStream(context, value.stream(begin.longValue(), null));
            }
            if (end >= 0) {
                if (end <= begin) {
                    // slice(3, 1) -- can't have indexes not in order, result is empty
                    return jArray.create();
                }
                // slice(1, 3)
                return JsonElementStreamer.fromTransformedStream(context, value.stream(begin.longValue(), end.longValue() - begin.longValue()));
            }
            // slice(1, -2)
            var result = jArray.create();
            // at least skip the start index and then convert to array
            value.stream(begin.longValue(), null).forEach(item -> jArray.add(result, item));
            for (int i = 0; i < -end; i++) {
                jArray.remove(result, jArray.size(result) - 1);
            }
            return result;
        }
        // begin < 0
        if (end != null && end >= 0) {
            // slice(-1, 3) -- if begin is negative, end must be negative too, result is empty
            return jArray.create();
        }
        // end == null || end < 0

        // slice(-1) / slice(-3, -1)
        // any negative indices means that we now need to convert the stream to an array to determine the size
        var arr = value.toJsonArray();
        var arrSize = jArray.size(arr);
        var result = jArray.create();
        for (int i = arrSize + begin; i < arrSize + (end == null ? 0 : end) ; i++) {
            jArray.add(result, jArray.get(arr, i));
        }
        return result;
    }
}
