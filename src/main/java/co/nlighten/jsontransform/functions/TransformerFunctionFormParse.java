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
 * @see TransformerFunctionFormParseTest
 */
@Aliases("formparse")
@Documentation(value = "Parses a Form URL-Encoded string to `object`",
               notes = "Every element will have 2 forms in the result object:\n" +
                       "singular with its original query name (e.g. q)\n" +
                       "plural with its name suffixed with $$ (e.g. q$$)")
@InputType(ArgType.String)
@OutputType(ArgType.Object)
public class TransformerFunctionFormParse<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    private final FormUrlEncodedFormat<JE, JA, JO> formUrlFormat;

    public TransformerFunctionFormParse(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
        this.formUrlFormat = new FormUrlEncodedFormat<>(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        return formUrlFormat.deserialize(context.getString(null));
    }
}
