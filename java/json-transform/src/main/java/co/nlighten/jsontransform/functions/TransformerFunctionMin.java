package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.ArgumentType;

import java.util.Comparator;

@ArgumentType(value = "default", type = ArgType.Object, position = 0)
@ArgumentType(value = "by", type = ArgType.Transformer, position = 2, defaultString = "##current")
@ArgumentType(value = "type", type = ArgType.Enum, position = 1, defaultEnum = "AUTO")
public class TransformerFunctionMin<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {

    public TransformerFunctionMin(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var streamer = context.getJsonElementStreamer(null);
        if (streamer == null || streamer.knownAsEmpty())
            return null;
        var by = context.getJsonElement("by", true);

        var type = context.getEnum("type");

        var def = context.getJsonElement("default",true);
        Comparator<JE> comparator = type == null || "AUTO".equals(type)
                                    ? adapter.comparator()
                                    : switch (type) {
            case "NUMBER" -> Comparator.comparing(adapter::getNumberAsBigDecimal);
            case "BOOLEAN" -> Comparator.comparing(adapter::getBoolean);
            //case "string"
            default -> Comparator.comparing(adapter::getAsString);
        };
        return streamer.stream()
                .map(t -> {
                    var res = !adapter.isNull(by) ? context.transformItem(by, t) : t;
                    return adapter.isNull(res) ? def : res;
                })
                .min(comparator)
                .orElse(adapter.jsonNull());
    }
}
