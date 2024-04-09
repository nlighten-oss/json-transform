package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

@Aliases("unflatten")
@Documentation("Accepts an objet with dot separated field names and merges them into an hierarchical object")
@InputType(ArgType.Object)
@OutputType(ArgType.Object)
@ArgumentType(value = "target",
              type = ArgType.Object,
              position = 0,
              description = "A target to merge into",
              defaultIsNull = true)
@ArgumentType(value = "path",
              type = ArgType.String,
              position = 1,
              description = "The root path in the target to merge into",
              defaultIsNull = true)
public class TransformerFunctionUnflatten<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {

    public TransformerFunctionUnflatten(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        JO target;
        var targetValue = context.getJsonElement("target");
        if (OBJECT.is(targetValue)) {
            target = OBJECT.convert(targetValue);
        } else {
            target = OBJECT.create();
        }

        var source = context.getJsonElement(null, true);
        var path = context.getString("path", true);
        if (OBJECT.is(source)) {
            OBJECT.entrySet(OBJECT.convert(source))
                    .forEach(ke -> adapter.mergeInto(target, ke.getValue(),
                                        (path != null ? path + '.' : "") + ke.getKey()));
        }

        return target;
    }
}
