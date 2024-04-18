package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.*;

/*
 * For tests
 * @see TransformerFunctionTransformTest
 */
@Aliases("transform")
@Documentation("Applies a transformation inside a transformer (Useful for piping functions results)")
@InputType(ArgType.Any)
@ArgumentType(value = "to", type = ArgType.Transformer, position = 0, defaultIsNull = true,
              description = "Transformer to apply on input (inputs: ##current)")
@OutputType(ArgType.Any)
public class TransformerFunctionTransform<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionTransform(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var input = context.getJsonElement(null);
        var to = context.getJsonElement("to", false);
        return context.transformItem(to, input);
    }
}
