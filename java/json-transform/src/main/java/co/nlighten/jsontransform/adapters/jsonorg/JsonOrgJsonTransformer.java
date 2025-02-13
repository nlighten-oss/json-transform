package co.nlighten.jsontransform.adapters.jsonorg;

import co.nlighten.jsontransform.JsonTransformer;
import co.nlighten.jsontransform.JsonTransformerConfiguration;
import co.nlighten.jsontransform.TransformerFunctionsAdapter;

public class JsonOrgJsonTransformer extends JsonTransformer {

    private static JsonOrgJsonAdapter getJsonOrgAdapter() {
        var currentAdapter = JsonTransformerConfiguration.get().getAdapter();
        if (currentAdapter instanceof JsonOrgJsonAdapter joa) {
            return joa;
        }
        return new JsonOrgJsonAdapter();
    }

    public JsonOrgJsonTransformer(final Object definition) {
        super(definition, getJsonOrgAdapter());
    }

    public JsonOrgJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, getJsonOrgAdapter(), functionsAdapter);
    }
}