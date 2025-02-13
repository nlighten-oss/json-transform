package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.adapters.pojo.PojoJsonTransformerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JsonTransformerConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JsonTransformerConfiguration.class);
    static volatile JsonTransformerConfiguration current = null;

    public synchronized static void set(JsonTransformerConfiguration configuration) {
        if (configuration == null) {
            throw new ExceptionInInitializerError("Cannot set empty configuration");
        }
        log.debug("Setting configuration to {}", configuration);
        current = configuration;
    }

    public static JsonTransformerConfiguration get() {
        if (current == null) {
            log.debug("Json transformers configuration was not set. Using default Pojo implementation.");
            set(new PojoJsonTransformerConfiguration());
        }
        return current;
    }

    private final JsonAdapter<?, ?, ?> adapter;

    public JsonTransformerConfiguration(JsonAdapter<?, ?, ?> adapter) {
        this.adapter = adapter;
    }

    public JsonAdapter<?, ?, ?> getAdapter() {
        return this.adapter;
    }
}
