package co.nlighten.jsontransform.functions.common;

import co.nlighten.jsontransform.JsonTransformerUtils;
import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.JsonTransformerFunction;
import co.nlighten.jsontransform.ParameterResolver;

public class ObjectFunctionContext<JE, JA extends Iterable<JE>, JO extends JE> extends FunctionContext<JE, JA, JO> {
    private final JO definition;

    public ObjectFunctionContext(String path,
                                 JO definition, JsonAdapter<JE, JA, JO> jsonAdapter,
                                 String functionKey,
                                 TransformerFunction<JE, JA, JO> function,
                                 ParameterResolver resolver, JsonTransformerFunction<JE> extractor) {
        super(path, jsonAdapter, functionKey, function, resolver, extractor, definition);
        this.definition = definition;
    }

    @Override
    public boolean has(String name) {
        return jObject.has(definition, name);
    }

    @Override
    public Object get(String name, boolean transform) {
        var el = jObject.get(definition, name == null ? alias : name);
        if (adapter.isNull(el)) {
            return function.getDefaultValue(name);
        }
        return transform ? extractor.transform(getPathFor(name), el, resolver, true) : el;
    }

    @Override
    public String getPathFor(String name) {
        return path + JsonTransformerUtils.toObjectFieldPath(adapter, name == null ? getAlias() : name);
    }
}
