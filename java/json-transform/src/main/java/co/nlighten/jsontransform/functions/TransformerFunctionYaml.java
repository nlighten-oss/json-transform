package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.formats.yaml.YamlFormat;

public class TransformerFunctionYaml extends TransformerFunction {

    public TransformerFunctionYaml() {
        super();
    }
    @Override
    public Object apply(FunctionContext context) {
        return new YamlFormat(context.getAdapter()).serialize(context.getUnwrapped(null, true));
    }
}
