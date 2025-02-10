package co.nlighten.jsontransform.adapters.jsonorg;

import co.nlighten.jsontransform.JsonTransformerConfiguration;

public class JsonOrgJsonTransformerConfiguration extends JsonTransformerConfiguration {
    public JsonOrgJsonTransformerConfiguration() {
        super(new JsonOrgJsonAdapter());
    }
}
