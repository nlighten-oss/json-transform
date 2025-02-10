package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JsonTransformerConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JsonTransformerConfiguration.class);
    static volatile JsonTransformerConfiguration current = null;

    public synchronized static void set(JsonTransformerConfiguration configuration) {
        if(configuration == null) {
            throw new ExceptionInInitializerError("Cannot set empty configuration");
        }
        log.debug("Setting configuration to {}", configuration);
        current = configuration;
    }

    public static JsonTransformerConfiguration get() {
        if (current == null) {
            throw new ExceptionInInitializerError("Configuration not set");
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

    public DebuggableTransformerFunctions getDebuggableAdapter() {
        return new DebuggableTransformerFunctions(this.adapter);
    }

    public TransformerFunctionsAdapter getFunctionsAdapter() {
        return new TransformerFunctions(this.adapter);
    }
}
