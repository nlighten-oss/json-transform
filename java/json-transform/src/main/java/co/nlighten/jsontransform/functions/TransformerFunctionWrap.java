package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionWrap extends TransformerFunction {
    public TransformerFunctionWrap() {
        super(FunctionDescription.of(
                Map.of(
                    "prefix", ArgumentType.of(ArgType.String).position(0).defaultString(""),
                    "suffix", ArgumentType.of(ArgType.String).position(1).defaultString("")
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var res = context.getString(null);
        if (res == null)
            return null;
        return context.getString("prefix") + res + context.getString("suffix");
    }
}
