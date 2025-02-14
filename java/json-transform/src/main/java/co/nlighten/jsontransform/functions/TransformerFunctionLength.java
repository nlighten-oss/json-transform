package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.JsonElementStreamer;
import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionLength extends TransformerFunction {

    public TransformerFunctionLength() {
        super(FunctionDescription.of(
            Map.of(
            "type", ArgumentType.of(ArgType.Enum).position(0).defaultEnum("AUTO"),
            "default_zero", ArgumentType.of(ArgType.Boolean).position(1).defaultBoolean(false)
            )
        ));
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var type = context.getEnum("type");
        var defaultZero = context.getBoolean("default_zero");
        var adapter = context.getAdapter();
        switch (type) {
            case "STRING" -> {
                var je = context.getJsonElement(null);
                if (adapter.isJsonString(je)) {
                    return context.getAsString(je).length();
                }
            }
            case "ARRAY" -> {
                var obj = context.get(null);
                if (obj instanceof JsonElementStreamer jes) {
                    return jes.stream().count();
                }
                if (obj != null) {
                    var el = context.getAdapter().wrap(obj);
                    if (adapter.isJsonArray(el)) {
                        return adapter.size(el);
                    }
                }
            }
            case "OBJECT" -> {
                var obj = context.getJsonElement(null);
                if (adapter.isJsonObject(obj)) {
                    return adapter.size(obj);
                }
            }
            default -> {
                // AUTO (or null)
                var obj = context.get(null);
                if (obj instanceof JsonElementStreamer jes) {
                    return jes.stream().count();
                }
                var je = context.getAdapter().wrap(obj);
                if (adapter.isJsonObject(je) || adapter.isJsonArray(je)) {
                    return adapter.size(je);
                }
                if (adapter.isJsonString(obj)) {
                    return context.getAsString(obj).length();
                }
            }
        }
        return defaultZero ? 0 : null;
    }
}
