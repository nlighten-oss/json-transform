package co.nlighten.jsontransform.adapters.jackson;

import co.nlighten.jsontransform.JsonTransformer;
import co.nlighten.jsontransform.JsonTransformerConfiguration;
import co.nlighten.jsontransform.TransformerFunctionsAdapter;

public class JacksonJsonTransformer extends JsonTransformer {

    private static JacksonJsonAdapter getJacksonAdapter() {
        var currentAdapter = JsonTransformerConfiguration.get().getAdapter();
        if (currentAdapter instanceof JacksonJsonAdapter jja) {
            return jja;
        }
        return new JacksonJsonAdapter();
    }

    public JacksonJsonTransformer(final Object definition) {
        super(definition, getJacksonAdapter());
    }

    public JacksonJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, getJacksonAdapter(), functionsAdapter);
    }
}
