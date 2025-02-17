package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionBoolean extends TransformerFunction {
    public TransformerFunctionBoolean() {
        super(FunctionDescription.of(
            Map.of(
            "style", ArgumentType.of(ArgType.Enum).position(0).defaultEnum("JAVA")
            )
        ));
    }

    @Override
    public Object apply(FunctionContext context) {
        var jsStyle = "JS".equals(context.getEnum("style"));
        var adapter = context.getAdapter();
        return adapter.isTruthy(context.getUnwrapped(null), jsStyle);
    }
}
