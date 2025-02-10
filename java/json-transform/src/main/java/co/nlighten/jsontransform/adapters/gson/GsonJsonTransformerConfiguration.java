package co.nlighten.jsontransform.adapters.gson;

import co.nlighten.jsontransform.JsonTransformerConfiguration;

public class GsonJsonTransformerConfiguration extends JsonTransformerConfiguration {
    public GsonJsonTransformerConfiguration() {
        super(new GsonJsonAdapter());
    }
}
