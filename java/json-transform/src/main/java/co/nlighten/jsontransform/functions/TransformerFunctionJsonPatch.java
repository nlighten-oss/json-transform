package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.*;
import co.nlighten.jsontransform.manipulation.JsonPatch;

import java.util.Map;

/*
 * For tests
 * @see TransformerFunctionJsonPatchTest
 */
public class TransformerFunctionJsonPatch extends TransformerFunction {

    public TransformerFunctionJsonPatch() {
        super(FunctionDescription.of(
                Map.of(
                        "ops", ArgumentType.of(ArgType.Array).position(0)
                )
        ));
    }

    @Override
    public Object apply(FunctionContext context) {
        var source = context.getJsonElement(null);
        if (source == null) {
            return null;
        }
        return new JsonPatch(context.getAdapter()).patch(context.getJsonArray("ops"), source);
    }
}
