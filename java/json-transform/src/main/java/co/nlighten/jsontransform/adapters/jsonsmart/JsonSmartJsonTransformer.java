package co.nlighten.jsontransform.adapters.jsonsmart;

import co.nlighten.jsontransform.JsonTransformer;
import co.nlighten.jsontransform.JsonTransformerConfiguration;
import co.nlighten.jsontransform.TransformerFunctionsAdapter;

public class JsonSmartJsonTransformer extends JsonTransformer {

    public static JsonSmartJsonAdapter DEFAULT_ADAPTER = new JsonSmartJsonAdapter();

    public static JsonSmartJsonAdapter getAdapter() {
        var currentAdapter = JsonTransformerConfiguration.get().getAdapter();
        if (currentAdapter instanceof JsonSmartJsonAdapter joa) {
            return joa;
        }
        return DEFAULT_ADAPTER;
    }

    public JsonSmartJsonTransformer(final Object definition) {
        super(definition, getAdapter());
    }

    public JsonSmartJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, getAdapter(), functionsAdapter);
    }
}