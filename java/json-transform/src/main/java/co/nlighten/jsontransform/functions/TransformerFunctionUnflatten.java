package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Map;

public class TransformerFunctionUnflatten extends TransformerFunction {

    public TransformerFunctionUnflatten() {
        super(FunctionDescription.of(
                Map.of(
                        "target", ArgumentType.of(ArgType.Object).position(0).defaultIsNull(true),
                        "path", ArgumentType.of(ArgType.String).position(1).defaultIsNull(true)
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        Object target;
        var adapter = context.getAdapter();
        var targetValue = context.getJsonElement("target");
        if (adapter.isJsonObject(targetValue)) {
            target = targetValue;
        } else {
            target = adapter.createObject();
        }

        var source = context.getJsonElement(null, true);
        var path = context.getString("path", true);
        if (adapter.isJsonObject(source)) {
            adapter.entrySet(source)
                    .forEach(ke -> adapter.mergeInto(target, ke.getValue(),
                                        (path != null ? path + '.' : "") + ke.getKey()));
        }

        return target;
    }
}
