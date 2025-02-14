package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionTransform extends TransformerFunction {
    public TransformerFunctionTransform() {
        super(FunctionDescription.of(
            Map.of(
            "to", ArgumentType.of(ArgType.Transformer).position(0).defaultIsNull(true)
            )
        ));
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var input = context.getJsonElement(null);
        var to = context.getJsonElement("to", false);
        return context.transformItem(to, input);
    }
}
