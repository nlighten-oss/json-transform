package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.formats.formurlencoded.FormUrlEncodedFormat;

public class TransformerFunctionFormParse extends TransformerFunction {

    public TransformerFunctionFormParse() {
        super();
    }

    @Override
    public Object apply(FunctionContext context) {
        // TODO: how to create the format once?
        return new FormUrlEncodedFormat(context.getAdapter()).deserialize(context.getString(null));
    }
}
