package co.nlighten.jsontransform.adapters.jsonorg;

import co.nlighten.jsontransform.JsonTransformer;
import co.nlighten.jsontransform.JsonTransformerConfiguration;
import co.nlighten.jsontransform.TransformerFunctionsAdapter;

public class JsonOrgJsonTransformer extends JsonTransformer {

    public static JsonOrgJsonAdapter DEFAULT_ADAPTER = new JsonOrgJsonAdapter();

    public static JsonOrgJsonAdapter getAdapter() {
        var currentAdapter = JsonTransformerConfiguration.get().getAdapter();
        if (currentAdapter instanceof JsonOrgJsonAdapter joa) {
            return joa;
        }
        return DEFAULT_ADAPTER;
    }

    public JsonOrgJsonTransformer(final Object definition) {
        super(definition, getAdapter());
    }

    public JsonOrgJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, getAdapter(), functionsAdapter);
    }
}