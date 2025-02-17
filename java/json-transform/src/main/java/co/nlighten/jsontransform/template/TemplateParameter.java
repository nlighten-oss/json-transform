package co.nlighten.jsontransform.template;

import co.nlighten.jsontransform.*;
import co.nlighten.jsontransform.adapters.JsonAdapter;

public class TemplateParameter {
    private final String name;
    private String mDefault;

    String getDefault() {
        return mDefault;
    }

    void setDefault(String value) {
        mDefault = value;
    }

    TemplateParameter(String name, String defaultValue) {
        this.name = name;
        mDefault = defaultValue == null ? "" : defaultValue;
    }

    String getStringValue(ParameterResolver resolver, JsonAdapter<?,?,?> adapter) {
        if (resolver == null) {
            return null;
        }

        var val = resolver.get(name);

        if (name.startsWith("$$") && (name.equals(val) || val == null)) {
            var x = new TextTemplateJsonTransformer(adapter);
            val = x.transformString(adapter.wrap(name), resolver);
        }

        return val == null || adapter.isNull(val)
                ? mDefault
                : adapter.getAsString(val).replace("{", "\\{");
    }
}