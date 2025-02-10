package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.formats.formurlencoded.FormUrlEncodedFormat;

/*
 * For tests
 * @see TransformerFunctionFormTest
 */
public class TransformerFunctionForm extends TransformerFunction {

    public TransformerFunctionForm() {
        super();
    }

    @Override
    public Object apply(FunctionContext context) {
        // TODO: how to create the format once?
        return new FormUrlEncodedFormat(context.getAdapter()).serialize(context.getUnwrapped(null));
    }
}
