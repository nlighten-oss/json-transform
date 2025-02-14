package co.nlighten.jsontransform.functions.common;

import co.nlighten.jsontransform.JsonTransformerFunction;
import co.nlighten.jsontransform.ParameterResolver;
import co.nlighten.jsontransform.adapters.JsonAdapter;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class InlineFunctionContext extends FunctionContext {
    protected final String stringInput;
    protected final ArrayList<Object> args;

    public InlineFunctionContext(String path, String input, ArrayList<Object> args,
                                 JsonAdapter<?, ?, ?> jsonAdapter,
                                 String functionKey,
                                 TransformerFunction function,
                                 ParameterResolver resolver, JsonTransformerFunction extractor) {
        super(path, jsonAdapter, functionKey, function, resolver, extractor, null);
        this.stringInput = input;
        this.args = args;
    }

    @Override
    public boolean has(String name) {
        var argument = function.getArgument(name);
        return name == null || (argument != null && argument.position > -1 && args != null && args.size() > argument.position);
    }

    @Override
    public CompletionStage<Object> get(String name, boolean transform) {
        var argument = function.getArgument(name);
        if (name != null && (argument == null || argument.position < 0 || args == null || args.size() <= argument.position)) {
            return CompletableFuture.completedStage(function.getDefaultValue(name));
        }
        var argValue = name == null ? stringInput : args.get(argument.position);
        if (argValue instanceof String s && transform) {
            return extractor.transform(getPathFor(name), adapter.wrap(s), resolver, true);
        }
        if (adapter.is(argValue)) {
            return transform
                    ? extractor.transform(getPathFor(name), argValue, resolver, true)
                    : CompletableFuture.completedStage(argValue);
        }
        return CompletableFuture.completedStage(argValue);
    }

    @Override
    public String getPathFor(String name) {
        return path + (name == null ? "" : "(" + name + ")");
    }
}
