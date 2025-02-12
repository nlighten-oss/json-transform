package co.nlighten.jsontransform.adapters.pojo;

import co.nlighten.jsontransform.JsonTransformerConfiguration;

public class PojoJsonTransformerConfiguration extends JsonTransformerConfiguration {
    public PojoJsonTransformerConfiguration() {
        super(new PojoJsonAdapter());
    }
}
