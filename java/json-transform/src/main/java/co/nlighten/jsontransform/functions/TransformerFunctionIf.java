package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionIf extends TransformerFunction {
    public TransformerFunctionIf() {
        super(FunctionDescription.of(
            Map.of(
            "then", ArgumentType.of(ArgType.Any).position(0).defaultIsNull(true),
            "else", ArgumentType.of(ArgType.Any).position(1).defaultIsNull(true)
            )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        boolean condition;
        var adapter = context.getAdapter();
        if (context.has("then")) {
            var value = context.getJsonElement(null);
            if (adapter.isTruthy(value)) {
                return context.getJsonElement("then", true);
            } else if (context.has("else")) {
                return context.getJsonElement("else", true);
            }
        } else {
            var arr = context.getJsonArray(null);
            if (arr == null || adapter.size(arr) < 2)
                return null;
            var cje = adapter.get(arr, 0);
            if (adapter.isNull(cje)) {
                condition = false;
            } else if (adapter.isJsonBoolean(cje)) {
                condition = adapter.getBoolean(cje);
            } else {
                condition = adapter.isTruthy(context.transform(context.getPathFor(0), cje));
            }

            if (condition) {
                return context.transform(context.getPathFor(1), adapter.get(arr, 1));
            } else if (adapter.size(arr) > 2) {
                return context.transform(context.getPathFor(2), adapter.get(arr, 2));
            }
        }
        return null; // default falsy value
    }
}
