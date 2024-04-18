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
 * @see TransformerFunctionRawTest
 */
@Aliases("raw")
@Documentation("Returns the input as-is without interpreting transformers")
@InputType(ArgType.Any)
@OutputType(ArgType.Any)
public class TransformerFunctionRaw<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    public TransformerFunctionRaw(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        return context.getJsonElement(null, false);
    }
}
