package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.manipulation.JsonPatch;
import co.nlighten.jsontransform.functions.annotations.*;

/*
 * For tests
 * @see TransformerFunctionJsonPatchTest
 */
@Aliases({"jsonpatch"})
@Documentation("Apply patches defined by JSON Patch RFC-6902")
@InputType(ArgType.Any)
@ArgumentType(value = "ops", type = ArgType.Array, position = 0, required = true,
              description = "A list of operations")
@OutputType(ArgType.Any)
public class TransformerFunctionJsonPatch<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {

    private final JsonPatch<JE, JA, JO> jsonPatch;

    public TransformerFunctionJsonPatch(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
        this.jsonPatch = new JsonPatch<>(adapter);
    }

    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var source = context.getJsonElement(null);
        if (source == null) {
            return null;
        }
        return jsonPatch.patch(context.getJsonArray("ops"), source);
    }
}
