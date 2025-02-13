package co.nlighten.jsontransform.adapters.gson;

import co.nlighten.jsontransform.JsonTransformer;
import co.nlighten.jsontransform.JsonTransformerConfiguration;
import co.nlighten.jsontransform.TransformerFunctionsAdapter;

public class GsonJsonTransformer extends JsonTransformer {

    private static GsonJsonAdapter getGsonAdapter() {
        var currentAdapter = JsonTransformerConfiguration.get().getAdapter();
        if (currentAdapter instanceof GsonJsonAdapter gja) {
            return gja;
        }
        return new GsonJsonAdapter();
    }

    public GsonJsonTransformer(final Object definition) {
        super(definition, getGsonAdapter());
    }

    public GsonJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, getGsonAdapter(), functionsAdapter);
    }
}
