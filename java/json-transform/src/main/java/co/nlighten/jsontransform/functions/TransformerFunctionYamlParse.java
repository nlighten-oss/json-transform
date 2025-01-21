package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.formats.yaml.YamlFormat;

/*
 * For tests
 * @see TransformerFunctionYamlParseTest
 */
public class TransformerFunctionYamlParse<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {

    private final YamlFormat<JE, JA, JO> yamlFormat;

    public TransformerFunctionYamlParse(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
        this.yamlFormat = new YamlFormat<>(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        return yamlFormat.deserialize(context.getString(null));
    }
}
