package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.formats.formurlencoded.FormUrlEncodedFormat;

/*
 * For tests
 * @see TransformerFunctionFormTest
 */
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
