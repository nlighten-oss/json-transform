package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.JsonElementStreamer;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

@Aliases("length")
@Documentation("Returns the length of a value")
@InputType(ArgType.Any)
@ArgumentType(value = "type", type = ArgType.Enum, position = 0, defaultEnum = "AUTO",
              enumValues = { "AUTO", "STRING", "ARRAY", "OBJECT" },
              description = "Restrict the type of value to check length of (if specified type no detected the result will be null)")
@ArgumentType(value = "default_zero", type = ArgType.Boolean, position = 1, defaultBoolean = false,
              description = "Whether to return 0 instead of null (on any kind of issue)")
@OutputType(ArgType.Integer)
public class TransformerFunctionLength<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {

    public TransformerFunctionLength(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var type = context.getEnum("type");
        var defaultZero = context.getBoolean("default_zero");
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
                    var el = context.wrap(obj);
                    if (jArray.is(el)) {
                        return jArray.size((JA)el);
                    }
                }
            }
            case "OBJECT" -> {
                var obj = context.getJsonElement(null);
                if (jObject.is(obj)) {
                    return jObject.size((JO)obj);
                }
            }
            default -> {
                // AUTO (or null)
                var obj = context.get(null);
                if (obj instanceof JsonElementStreamer jes) {
                    return jes.stream().count();
                }
                var je = adapter.is(obj) ? (JE)obj : context.wrap(obj);
                if (jObject.is(je)) {
                    return jObject.size((JO)je);
                }
                if (jArray.is(je)) {
                    return jArray.size((JA)je);
                }
                if (adapter.isJsonString(obj)) {
                    return context.getAsString(obj).length();
                }
            }
        }
        return defaultZero ? 0 : null;
    }
}
