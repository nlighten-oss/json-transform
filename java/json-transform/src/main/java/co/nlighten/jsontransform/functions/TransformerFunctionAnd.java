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
 * @see TransformerFunctionAndTest
 */
@Aliases("and")
@Documentation("Evaluates to `true` if all values provided will evaluate to `true` (using the [Truthy logic]).")
@InputType(value = ArgType.Array, description = "Values to check")
@OutputType(ArgType.Boolean)
public class TransformerFunctionAnd<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionAnd(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }

    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var value = context.getJsonElementStreamer(null);
        if (value == null) {
            return false;
        }
        return value.stream().allMatch(adapter::isTruthy);
    }
}
