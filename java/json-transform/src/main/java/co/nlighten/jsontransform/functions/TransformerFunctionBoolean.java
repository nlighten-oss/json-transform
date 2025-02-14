package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;
import java.util.concurrent.CompletionStage;

public class TransformerFunctionBoolean extends TransformerFunction {
    public TransformerFunctionBoolean() {
        super(FunctionDescription.of(
            Map.of(
            "style", ArgumentType.of(ArgType.Enum).position(0).defaultEnum("JAVA")
            )
        ));
    }

    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        return context.getEnum("style").thenApply(style -> {
            var jsStyle = "JS".equals(style);
            var adapter = context.getAdapter();
            return context.getUnwrapped(null).thenApply(value ->
                    adapter.isTruthy(value, jsStyle));
        });
    }
}
