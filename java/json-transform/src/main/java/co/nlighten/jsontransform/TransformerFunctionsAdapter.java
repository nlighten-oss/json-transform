package co.nlighten.jsontransform;

public interface TransformerFunctionsAdapter<JE, JA extends Iterable<JE>, JO extends JE> {
    TransformerFunctions.FunctionMatchResult<Object> matchInline(String path, String value, ParameterResolver resolver, JsonTransformerFunction<JE> transformer);

    TransformerFunctions.FunctionMatchResult<Object> matchObject(String path, JO definition, ParameterResolver resolver, JsonTransformerFunction<JE> transformer);
}
