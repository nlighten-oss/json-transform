package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.functions.common.ArgType;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import co.nlighten.jsontransform.formats.yaml.YamlFormat;
import co.nlighten.jsontransform.functions.annotations.Aliases;
import co.nlighten.jsontransform.functions.annotations.Documentation;
import co.nlighten.jsontransform.functions.annotations.InputType;
import co.nlighten.jsontransform.functions.annotations.OutputType;

/*
 * For tests
 * @see TransformerFunctionYamlTest
 */
@Aliases("yaml")
@Documentation(value = "Converts an object to YAML format")
@InputType(ArgType.Object)
@OutputType(ArgType.String)
public class TransformerFunctionYaml<JE, JA extends Iterable<JE>, JO extends JE> extends TransformerFunction<JE, JA, JO> {
    private final YamlFormat<JE,JA,JO> yamlFormat;

    public TransformerFunctionYaml(JsonAdapter<JE, JA, JO> adapter) {
        super(adapter);
        this.yamlFormat = new YamlFormat<>(adapter);
    }
    @Override
    public Object apply(FunctionContext<JE, JA, JO> context) {
        return yamlFormat.serialize(context.getUnwrapped(null, true));
    }
}
