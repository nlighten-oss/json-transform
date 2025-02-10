package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;

import java.util.Comparator;
import java.util.Map;

public class TransformerFunctionMax extends TransformerFunction {

    public TransformerFunctionMax() {
        super(FunctionDescription.of(
                Map.of(
                        "default", ArgumentType.of(ArgType.Object).position(0),
                        "by", ArgumentType.of(ArgType.Transformer).position(2).defaultIsNull(true),
                        "type", ArgumentType.of(ArgType.Enum).position(1).defaultEnum("AUTO")
                )
        ));
    }
    @Override
    public Object apply(FunctionContext context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null || streamer.knownAsEmpty())
            return null;
        var by = context.getJsonElement("by", true);

        var type = context.getEnum("type");

        var def = context.getJsonElement("default",true);
        var adapter = context.getAdapter();
        Comparator<Object> comparator = type == null || "AUTO".equals(type)
                         ? adapter.comparator()
                         : switch (type) {
            case "NUMBER" -> Comparator.comparing(adapter::getNumberAsBigDecimal);
            case "BOOLEAN" -> Comparator.comparing(adapter::getBoolean);
            //case "string"
            default -> Comparator.comparing(adapter::getAsString);
        };
        var result = streamer.stream()
                .map(t -> {
                    var res = !adapter.isNull(by) ? context.transformItem(by, t) : t;
                    return (adapter.isNull(res)) ? def : res;
                })
                .max(comparator);
        if (result.isPresent()) {
            var resultValue = result.get();
            if ("NUMBER".equals(type) || adapter.isJsonNumber(resultValue)) {
                return adapter.getNumberAsBigDecimal(resultValue);
            } else if ("BOOLEAN".equals(type) || adapter.isJsonBoolean(resultValue)) {
                return adapter.getBoolean(resultValue);
            } else {
                return adapter.getAsString(resultValue);
            }
        }
        return adapter.jsonNull();
    }
}
