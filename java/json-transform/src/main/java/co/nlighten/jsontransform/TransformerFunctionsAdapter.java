package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;

public interface TransformerFunctionsAdapter {
    TransformerFunctions.FunctionMatchResult<Object> matchInline(JsonAdapter<?,?,?> adapter, String path, String value, ParameterResolver resolver, JsonTransformerFunction transformer);

    TransformerFunctions.FunctionMatchResult<Object> matchObject(JsonAdapter<?,?,?> adapter, String path, Object definition, ParameterResolver resolver, JsonTransformerFunction transformer);
}
