package co.nlighten.jsontransform.functions.common;

import co.nlighten.jsontransform.JsonTransformerUtils;
import co.nlighten.jsontransform.JsonTransformerFunction;
import co.nlighten.jsontransform.ParameterResolver;
import co.nlighten.jsontransform.adapters.JsonAdapter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ObjectFunctionContext extends FunctionContext {
    private final Object definition;

    public ObjectFunctionContext(String path,
                                 Object definition,
                                 JsonAdapter<?, ?, ?> adapter,
                                 String functionKey,
                                 TransformerFunction function,
                                 ParameterResolver resolver, JsonTransformerFunction extractor) {
        super(path, adapter, functionKey, function, resolver, extractor, definition);
        this.definition = definition;
    }

    @Override
    public boolean has(String name) {
        return adapter.has(definition, name);
    }

    @Override
    public CompletionStage<Object> get(String name, boolean transform) {
        var el = adapter.get(definition, name == null ? alias : name);
        if (adapter.isNull(el)) {
            return CompletableFuture.completedStage(function.getDefaultValue(name));
        }
        return transform
                ? extractor.transform(getPathFor(name), el, resolver, true)
                : CompletableFuture.completedStage(el);
    }

    @Override
    public String getPathFor(String name) {
        return path + JsonTransformerUtils.toObjectFieldPath(adapter, name == null ? getAlias() : name);
    }
}
