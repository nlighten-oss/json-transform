package co.nlighten.jsontransform.adapters.gson;

import co.nlighten.jsontransform.JsonTransformer;
import co.nlighten.jsontransform.JsonTransformerConfiguration;
import co.nlighten.jsontransform.TransformerFunctionsAdapter;

public class GsonJsonTransformer extends JsonTransformer {

    public static GsonJsonAdapter DEFAULT_ADAPTER = new GsonJsonAdapter();

    public static GsonJsonAdapter getAdapter() {
        var currentAdapter = JsonTransformerConfiguration.get().getAdapter();
        if (currentAdapter instanceof GsonJsonAdapter gja) {
            return gja;
        }
        return DEFAULT_ADAPTER;
    }

    public GsonJsonTransformer(final Object definition) {
        super(definition, getAdapter());
    }

    public GsonJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, getAdapter(), functionsAdapter);
    }
}
