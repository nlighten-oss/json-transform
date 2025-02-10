package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.formats.yaml.YamlFormat;

/*
 * For tests
 * @see TransformerFunctionYamlParseTest
 */
public class TransformerFunctionYamlParse extends TransformerFunction {

    public TransformerFunctionYamlParse() {
        super();
    }

    @Override
    public Object apply(FunctionContext context) {
        return new YamlFormat(context.getAdapter()).deserialize(context.getString(null));
    }
}
