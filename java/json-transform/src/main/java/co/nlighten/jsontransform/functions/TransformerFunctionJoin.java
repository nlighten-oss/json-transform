package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/*
 * For tests
 * @see TransformerFunctionJoinTest
 */
public class TransformerFunctionJoin extends TransformerFunction {
    public TransformerFunctionJoin() {
        super(FunctionDescription.of(
            Map.of(
            "delimiter", ArgumentType.of(ArgType.String).position(0).defaultString(""),
            "prefix", ArgumentType.of(ArgType.String).position(1).defaultString(""),
            "suffix", ArgumentType.of(ArgType.String).position(2).defaultString(""),
            "keep_nulls", ArgumentType.of(ArgType.Boolean).position(3).defaultBoolean(false),
            // backward compatability
            "$$delimiter", ArgumentType.of(ArgType.String).position(0).defaultIsNull(true)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
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
