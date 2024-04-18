package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.functions.annotations.Aliases;
import co.nlighten.jsontransform.functions.annotations.Documentation;
import co.nlighten.jsontransform.functions.annotations.InputType;
import co.nlighten.jsontransform.functions.annotations.OutputType;

/*
 * For tests
 * @see TransformerFunctionXorTest
 */
@Aliases("xor")
@Documentation("Evaluates to `true` if only one of the values provided will evaluate to `true` (using the [Truthy logic])")
@InputType(ArgType.Array)
@OutputType(ArgType.Boolean)
public class TransformerFunctionXor<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionXor(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var value = context.getJsonElementStreamer(null);
        return value.stream().filter(adapter::isTruthy).count() == 1;
    }
}
