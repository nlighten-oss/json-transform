package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

import java.util.Objects;
import java.util.stream.Collectors;

/*
 * For tests
 * @see TransformerFunctionJoinTest
 */
@ArgumentType(value = "delimiter", type = ArgType.String, position = 0, defaultString = "", aliases = { "$$delimiter "})
@ArgumentType(value = "prefix", type = ArgType.String, position = 1, defaultString = "")
@ArgumentType(value = "suffix", type = ArgType.String, position = 2, defaultString = "")
@ArgumentType(value = "keep_nulls", type = ArgType.Boolean, position = 3, defaultBoolean = false)
public class TransformerFunctionJoin<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionJoin(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var arr = context.getJsonElementStreamer(null);
        var delimiter = context.getString("$$delimiter"); // backwards compat.
        if (delimiter == null) {
            delimiter = context.getString("delimiter");
        }
        var prefix = context.getString("prefix");
        var suffix = context.getString("suffix");
        var stream = arr.stream().map(context::getAsString);
        if (!context.getBoolean("keep_nulls")) {
            stream = stream.filter(Objects::nonNull);
        }
        return stream.collect(Collectors.joining(delimiter, prefix, suffix));
    }
}
