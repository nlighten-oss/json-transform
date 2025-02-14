package co.nlighten.jsontransform.adapters.jackson;

import co.nlighten.jsontransform.JsonTransformer;
import co.nlighten.jsontransform.JsonTransformerConfiguration;
import co.nlighten.jsontransform.TransformerFunctionsAdapter;

public class JacksonJsonTransformer extends JsonTransformer {

    public static JacksonJsonAdapter DEFAULT_ADAPTER = new JacksonJsonAdapter();

    public static JacksonJsonAdapter getAdapter() {
        var currentAdapter = JsonTransformerConfiguration.get().getAdapter();
        if (currentAdapter instanceof JacksonJsonAdapter jja) {
            return jja;
        }
        return DEFAULT_ADAPTER;
    }

    public JacksonJsonTransformer(final Object definition) {
        super(definition, getAdapter());
    }

    public JacksonJsonTransformer(final Object definition, TransformerFunctionsAdapter functionsAdapter) {
        super(definition, getAdapter(), functionsAdapter);
    }
}
