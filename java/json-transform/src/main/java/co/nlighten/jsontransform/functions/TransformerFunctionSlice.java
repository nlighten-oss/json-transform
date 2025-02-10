package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;
import co.nlighten.jsontransform.JsonElementStreamer;

import java.util.Map;

/*
 * For tests
 * @see TransformerFunctionSliceTest
 */
public class TransformerFunctionSlice extends TransformerFunction {
    public TransformerFunctionSlice() {
        super(FunctionDescription.of(
            Map.of(
            "begin", ArgumentType.of(ArgType.Integer).position(0).defaultInteger(0),
            "end", ArgumentType.of(ArgType.Integer).position(1).defaultIsNull(true)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var adapter = context.getAdapter();
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
                    return adapter.createArray();
                }
                // slice(1, 3)
                return JsonElementStreamer.fromTransformedStream(context, value.stream(begin.longValue(), end.longValue() - begin.longValue()));
            }
            // slice(1, -2)
            var result = adapter.createArray();
            // at least skip the start index and then convert to array
            value.stream(begin.longValue(), null).forEach(item -> adapter.add(result, item));
            for (int i = 0; i < -end; i++) {
                adapter.remove(result, adapter.size(result) - 1);
            }
            return result;
        }
        // begin < 0
        if (end != null && end >= 0) {
            // slice(-1, 3) -- if begin is negative, end must be negative too, result is empty
            return adapter.createArray();
        }
        // end == null || end < 0

        // slice(-1) / slice(-3, -1)
        // any negative indices means that we now need to convert the stream to an array to determine the size
        var arr = value.toJsonArray();
        var arrSize = adapter.size(arr);
        var result = adapter.createArray();
        for (int i = arrSize + begin; i < arrSize + (end == null ? 0 : end) ; i++) {
            adapter.add(result, adapter.get(arr, i));
        }
        return result;
    }
}
