package co.nlighten.jsontransform.adapters.pojo;

import co.nlighten.jsontransform.JsonTransformer;
import co.nlighten.jsontransform.JsonTransformerConfiguration;
import co.nlighten.jsontransform.TransformerFunctionsAdapter;

public class PojoJsonTransformer extends JsonTransformer {

    private static PojoJsonAdapter getPojoAdapter() {
        var currentAdapter = JsonTransformerConfiguration.get().getAdapter();
        if (currentAdapter instanceof PojoJsonAdapter pja) {
            return pja;
        }
        return new PojoJsonAdapter();
    }

    public PojoJsonTransformer(final Object definition) {
        super(definition, getPojoAdapter());
    }

    public PojoJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, getPojoAdapter(), functionsAdapter);
    }
}
