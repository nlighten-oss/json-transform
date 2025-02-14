package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;

import java.util.concurrent.CompletionStage;

public interface TransformerFunctionsAdapter {
    CompletionStage<TransformerFunctions.FunctionMatchResult<Object>> matchInline(JsonAdapter<?,?,?> adapter, String path, String value, ParameterResolver resolver, JsonTransformerFunction transformer);

    CompletionStage<TransformerFunctions.FunctionMatchResult<Object>> matchObject(JsonAdapter<?,?,?> adapter, String path, Object definition, ParameterResolver resolver, JsonTransformerFunction transformer);
}
