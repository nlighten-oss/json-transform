package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionFlatten extends TransformerFunction {

    public TransformerFunctionFlatten() {
        super(FunctionDescription.of(
            Map.of(
            "target", ArgumentType.of(ArgType.Object).position(0).defaultIsNull(true),
            "prefix", ArgumentType.of(ArgType.String).position(1).defaultIsNull(true),
            "array_prefix", ArgumentType.of(ArgType.String).position(2).defaultString("$")
            )
        ));
    }
    @Override
    public CompletionStage<Object> apply(FunctionContext context) {
        var jeTarget = context.getJsonElement("target");
        Object target;
        var adapter = context.getAdapter();
        if (adapter.isJsonObject(jeTarget)) {
            target = jeTarget;
        } else {
            target = adapter.createObject();
        }

        return flatten(context, context.getJsonElement(null, true), target, context.getString("prefix"),
                       context.getString("array_prefix"));
    }

    private Object flatten(FunctionContext context, Object source, Object target, String prefix, String arrayPrefix) {
        var adapter = context.getAdapter();
        if (adapter.isNull(source)) {
            return target;
        }
        if (adapter.isJsonObject(source)) {
            adapter.entrySet(source)
                    .forEach(es -> flatten(context, es.getValue(), target,
                                           prefix == null ? es.getKey() : (prefix + "." + es.getKey()), arrayPrefix));
        } else if (adapter.isJsonArray(source)) {
            if (arrayPrefix != null) {
                var size = adapter.size(source);
                for (var i = 0; i < size; i++) {
                    flatten(context, adapter.get(source, i), target, (prefix == null ? "" : (prefix + ".")) + arrayPrefix + i, arrayPrefix);
                }
            } else if (prefix != null) {
                adapter.add(target, prefix, source);
            }
        } else {
            if (prefix == null || prefix.isBlank()) {
                return source;
            }
            adapter.add(target, prefix, source);
        }
        return target;
    }
}
