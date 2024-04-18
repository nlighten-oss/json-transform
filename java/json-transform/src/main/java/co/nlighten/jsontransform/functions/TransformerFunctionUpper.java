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
 * @see TransformerFunctionUpperTest
 */
@Aliases("upper")
@Documentation("Converts a string to all uppercase letters")
@InputType(ArgType.String)
@OutputType(ArgType.String)
public class TransformerFunctionUpper<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionUpper(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        var result = context.getString(null);
        return result == null ? null : result.toUpperCase();
    }
}
