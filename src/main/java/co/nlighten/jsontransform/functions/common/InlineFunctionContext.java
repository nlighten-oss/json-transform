package co.nlighten.jsontransform.functions.common;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.JsonTransformerFunction;
import co.nlighten.jsontransform.ParameterResolver;

import java.util.ArrayList;

public class InlineFunctionContext<JE, JA extends Iterable<JE>, JO extends JE> extends FunctionContext<JE, JA, JO> {
    protected final String stringInput;
    protected final ArrayList<Object> args;

    public InlineFunctionContext(String input, ArrayList<Object> args,
                                 JsonAdapter<JE, JA, JO> jsonAdapter,
                                 String functionKey,
                                 TransformerFunction<JE, JA, JO> function,
                                 ParameterResolver resolver, JsonTransformerFunction<JE> extractor) {
        super(jsonAdapter, functionKey, function, resolver, extractor, null);
        this.stringInput = input;
        this.args = args;
    }

    @Override
    public boolean has(String name) {
        var argument = function.getArgument(name);
        return name == null || (argument != null && argument.position() > -1 && args != null && args.size() > argument.position());
    }

    @Override
    public Object get(String name, boolean transform) {
        var argument = function.getArgument(name);
        if (name != null && (argument == null || argument.position() < 0 || args == null || args.size() <= argument.position())) {
            return function.getDefaultValue(name);
        }
        var argValue = name == null ? stringInput : args.get(argument.position());
        if (argValue instanceof String s && transform) {
            return extractor.transform(adapter.wrap(s), resolver, true);
        }
        if (adapter.is(argValue)) {
            var je = (JE)argValue;
            return transform ? extractor.transform(je, resolver, true) : je;
        }
        return argValue;
    }
}
