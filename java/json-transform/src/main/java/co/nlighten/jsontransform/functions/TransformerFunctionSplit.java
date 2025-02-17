package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionSplit extends TransformerFunction {
    public TransformerFunctionSplit() {
        super(FunctionDescription.of(
                Map.of(
                        "delimiter", ArgumentType.of(ArgType.String).position(0).defaultString(""),
                        "limit", ArgumentType.of(ArgType.Integer).position(1).defaultInteger(0)
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var str = context.getString(null);
        if (str == null) {
            return null;
        }
        return str.split(context.getString("delimiter"), context.getInteger("limit"));
    }
}
