package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.JsonElementStreamer;
import co.nlighten.jsontransform.functions.annotations.Aliases;
import co.nlighten.jsontransform.functions.annotations.Documentation;
import co.nlighten.jsontransform.functions.annotations.InputType;
import co.nlighten.jsontransform.functions.annotations.OutputType;

/*
 * For tests
 *
 * @see TransformerFunctionValueTest
 */
@Aliases("value")
@Documentation("Returns the value if it passes the truthy test, or null otherwise")
@InputType(ArgType.Any)
@OutputType(ArgType.Any)
public class TransformerFunctionValue<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionValue(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var result = context.get(null, true);
        if (result instanceof JsonElementStreamer streamer) {
            result = streamer.toJsonArray();
        }
        if (adapter.isTruthy(result)) {
            return result;
        }
        return null;
    }
}
