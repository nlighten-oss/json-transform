package co.nlighten.jsontransform.adapters.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.JsonNodeFeature;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.function.Supplier;

public class JacksonHelpers {

    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .setDateFormat(new SimpleDateFormat(ISO_DATETIME_FORMAT))
                .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
                .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
                .configure(JsonNodeFeature.STRIP_TRAILING_BIGDECIMAL_ZEROES, true);
    }

    private static final Supplier<ObjectMapper> DEFAULT_JACKSON_SUPPLIER = JacksonHelpers::createObjectMapper;

    private static ThreadLocal<ObjectMapper> threadSafeFactory = ThreadLocal.withInitial(DEFAULT_JACKSON_SUPPLIER);

    public static ObjectMapper mapper() {
        return threadSafeFactory.get();
    }

    static com.jayway.jsonpath.Configuration setFactoryAndReturnJsonPathConfig(Supplier<ObjectMapper> jacksonSupplier) {
        if (jacksonSupplier != null) {
            threadSafeFactory = ThreadLocal.withInitial(jacksonSupplier);
        }
        var mapper = mapper();
        return new Configuration.ConfigurationBuilder()
                .jsonProvider(new JacksonJsonNodeJsonProvider(mapper))
                .mappingProvider(new JacksonMappingProvider(mapper))
                .options(Set.of(
                        Option.SUPPRESS_EXCEPTIONS
                ))
                .build();
    }
}
