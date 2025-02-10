package co.nlighten.jsontransform.adapters.jackson;

import co.nlighten.jsontransform.JsonTransformerConfiguration;

public class JacksonJsonTransformerConfiguration extends JsonTransformerConfiguration {
    public JacksonJsonTransformerConfiguration() {
        super(new JacksonJsonAdapter());
    }
}
