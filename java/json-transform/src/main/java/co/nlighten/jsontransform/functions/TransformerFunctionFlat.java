package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.JsonElementStreamer;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;

import java.util.Objects;
import java.util.stream.Stream;

/*
 * For tests
 * @see TransformerFunctionFlatTest
 */
public class TransformerFunctionFlat<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionFlat(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null) return null;

        return JsonElementStreamer.fromTransformedStream(context, streamer.stream()
            .flatMap(itm -> {
                if (adapter.isNull(itm)) {
                    return Stream.empty();
                } else if (jArray.is(itm)) {
                    return jArray.stream((JA)itm);
                }
                return Stream.of(itm);
            })
            .filter(Objects::nonNull));
    }
}
