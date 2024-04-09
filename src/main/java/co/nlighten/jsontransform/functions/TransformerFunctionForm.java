package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.formats.formurlencoded.FormUrlEncodedFormat;
import co.nlighten.jsontransform.functions.annotations.Aliases;
import co.nlighten.jsontransform.functions.annotations.Documentation;
import co.nlighten.jsontransform.functions.annotations.InputType;
import co.nlighten.jsontransform.functions.annotations.OutputType;

/*
 * For tests
 * @see TransformerFunctionFormTest
 */
@Aliases("form")
@Documentation(value = "Converts an object to Form URL-Encoded string (a.k.a Query String)",
               notes = "Array values will be treated as multiple values for the same key (so the key will be duplicated in the result for each of the values)")
@InputType(ArgType.Object)
@OutputType(ArgType.String)
public class TransformerFunctionForm<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {

    private final FormUrlEncodedFormat<JE, JA, JO> formUrlFormat;

    public TransformerFunctionForm(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
        this.formUrlFormat = new FormUrlEncodedFormat<>(adapter);
    }

    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        return formUrlFormat.serialize(context.getUnwrapped(null));
    }
}
