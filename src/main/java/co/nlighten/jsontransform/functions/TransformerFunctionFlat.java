package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.JsonElementStreamer;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.Aliases;
import co.nlighten.jsontransform.functions.annotations.Documentation;
import co.nlighten.jsontransform.functions.annotations.InputType;
import co.nlighten.jsontransform.functions.annotations.OutputType;

import java.util.Objects;
import java.util.stream.Stream;

/*
 * For tests
 * @see TransformerFunctionFlatTest
 */
@Aliases("flat")
@Documentation("Flatten an array of arrays (non array elements will remain, all `null` elements are removed from result)")
@InputType(ArgType.Array)
@OutputType(ArgType.Array)
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
                } else if (ARRAY.is(itm)) {
                    return ARRAY.stream((JA)itm);
                }
                return Stream.of(itm);
            })
            .filter(Objects::nonNull));
    }
}
