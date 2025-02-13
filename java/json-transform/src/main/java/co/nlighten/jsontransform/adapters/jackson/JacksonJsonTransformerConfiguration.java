package co.nlighten.jsontransform.adapters.jackson;

import co.nlighten.jsontransform.JsonTransformerConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.function.Supplier;

public class JacksonJsonTransformerConfiguration extends JsonTransformerConfiguration {
    public JacksonJsonTransformerConfiguration(Supplier<ObjectMapper> mapperSupplier) {
        super(new JacksonJsonAdapter(mapperSupplier));
    }
    public JacksonJsonTransformerConfiguration() {
        this(null);
    }
}
