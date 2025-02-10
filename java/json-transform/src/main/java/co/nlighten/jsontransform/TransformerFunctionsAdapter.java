package co.nlighten.jsontransform;

public interface TransformerFunctionsAdapter {
    TransformerFunctions.FunctionMatchResult<Object> matchInline(String path, String value, ParameterResolver resolver, JsonTransformerFunction transformer);

    TransformerFunctions.FunctionMatchResult<Object> matchObject(String path, Object definition, ParameterResolver resolver, JsonTransformerFunction transformer);
}
