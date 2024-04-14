package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

@Aliases("flatten")
@Documentation("Flattens a JsonObject into a flat dot seperated list of entries")
@InputType(ArgType.Object)
@OutputType(ArgType.Any)
@ArgumentType(value = "target",
              type = ArgType.Object,
              position = 0,
              description = "A target to merge into",
              defaultIsNull = true)
@ArgumentType(value = "prefix",
              type = ArgType.String,
              position = 1,
              description = "A prefix to add to the base",
              defaultIsNull = true)
@ArgumentType(value = "array_prefix",
              type = ArgType.String,
              position = 2,
              description = "Sets how array elements should be prefixed, leave null to not flatten arrays",
              defaultString = "$")
public class TransformerFunctionFlatten<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {

    public TransformerFunctionFlatten(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var jeTarget = context.getJsonElement("target");
        JO target;
        if (jObject.is(jeTarget)) {
            target = (JO)jeTarget;
        } else {
            target = jObject.create();
        }

        return flatten(context.getJsonElement(null, true), target, context.getString("prefix"),
                       context.getString("array_prefix"));
    }

    private JE flatten(JE source, JO target, String prefix, String arrayPrefix) {
        if (adapter.isNull(source)) {
            return target;
        }
        if (adapter.jObject.is(source)) {
            adapter.jObject.entrySet((JO)source)
                    .forEach(es -> flatten(es.getValue(), target,
                                           prefix == null ? es.getKey() : (prefix + "." + es.getKey()), arrayPrefix));
        } else if (adapter.jArray.is(source)) {
            var ja = (JA)source;
            if (arrayPrefix != null) {
                var size = adapter.jArray.size(ja);
                for (var i = 0; i < size; i++) {
                    flatten(adapter.jArray.get(ja, i), target, (prefix == null ? "" : (prefix + ".")) + arrayPrefix + i, arrayPrefix);
                }
            } else {
                adapter.jObject.add(target, prefix, ja);
            }
        } else {
            if (prefix == null || prefix.isBlank()) {
                return source;
            }
            adapter.jObject.add(target, prefix, source);
        }
        return target;
    }
}
