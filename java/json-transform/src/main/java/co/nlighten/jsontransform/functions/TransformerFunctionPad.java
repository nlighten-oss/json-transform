package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionPad extends TransformerFunction {
    public TransformerFunctionPad() {
        super(FunctionDescription.of(
            Map.of(
            "direction", ArgumentType.of(ArgType.Enum).position(0).defaultIsNull(true),
            "width", ArgumentType.of(ArgType.Integer).position(1).defaultIsNull(true),
            "pad_string", ArgumentType.of(ArgType.String).position(2).defaultString("0")
            )
        ));
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var res = context.getString(null);
        if (res == null)
            return null;

        var direction = context.getEnum("direction");
        var width = context.getInteger("width");
        if (direction == null || width == null || res.length() >= width) {
            return res;
        }

        var paddingSize = width - res.length();
        var padding = context.getString("pad_string").repeat(paddingSize);
        if (padding.length() > paddingSize) { // in case padding string is more than one character
            padding = padding.substring(0, paddingSize);
        }
        StringBuilder sb = new StringBuilder();
        if ("LEFT".equalsIgnoreCase(direction) || "START".equalsIgnoreCase(direction)) {
            sb.append(padding).append(res);
        } else if ("RIGHT".equalsIgnoreCase(direction) || "END".equalsIgnoreCase(direction)) {
            sb.append(res).append(padding);
        }
        return sb.toString();
    }
}
