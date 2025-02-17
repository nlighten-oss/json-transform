package co.nlighten.jsontransform.template;

import co.nlighten.jsontransform.JsonTransformer;
import co.nlighten.jsontransform.ParameterResolver;
import co.nlighten.jsontransform.adapters.JsonAdapter;

public class TextTemplateJsonTransformer extends JsonTransformer {
    public TextTemplateJsonTransformer(JsonAdapter<?, ?, ?> adapter) {
        super(null, adapter);
    }

    public Object transformString(Object definition, ParameterResolver resolver) {
        return fromJsonPrimitive("$", definition, resolver, false);
    }
}
