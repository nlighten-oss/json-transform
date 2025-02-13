package co.nlighten.jsontransform.adapters.jsonsmart;

import co.nlighten.jsontransform.JsonTransformerConfiguration;

public class JsonSmartJsonTransformerConfiguration extends JsonTransformerConfiguration {
    public JsonSmartJsonTransformerConfiguration() {
        super(new JsonSmartJsonAdapter());
    }
}
