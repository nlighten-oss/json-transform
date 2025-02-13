package co.nlighten.jsontransform.adapters.jsonsmart;

import co.nlighten.jsontransform.JsonTransformer;
import co.nlighten.jsontransform.JsonTransformerConfiguration;
import co.nlighten.jsontransform.TransformerFunctionsAdapter;

public class JsonSmartJsonTransformer extends JsonTransformer {

    private static JsonSmartJsonAdapter getJsonOrgAdapter() {
        var currentAdapter = JsonTransformerConfiguration.get().getAdapter();
        if (currentAdapter instanceof JsonSmartJsonAdapter joa) {
            return joa;
        }
        return new JsonSmartJsonAdapter();
    }

    public JsonSmartJsonTransformer(final Object definition) {
        super(definition, getJsonOrgAdapter());
    }

    public JsonSmartJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, getJsonOrgAdapter(), functionsAdapter);
    }
}