package co.nlighten.jsontransform.adapters.gson;

import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class GsonJsonPathConfigurator {

    private static final Logger log = LoggerFactory.getLogger(GsonJsonPathConfigurator.class);

    private static boolean initialized = false;

    private static class JaywayGSONConfiguration implements com.jayway.jsonpath.Configuration.Defaults {

        private static final JsonProvider jsonProvider = new GsonJsonProvider();
        private static final Set<Option> options = Set.of(
                Option.SUPPRESS_EXCEPTIONS
        );
        private static final MappingProvider mappingProvider = new GsonMappingProvider();

        @Override
        public JsonProvider jsonProvider() {
            return jsonProvider;
        }

        @Override
        public Set<Option> options() {
            return options;
        }

        @Override
        public MappingProvider mappingProvider() {
            return mappingProvider;
        }
    }

    private static com.jayway.jsonpath.Configuration.Defaults configurationDefaults = new JaywayGSONConfiguration();

    public static void setup() {
        if (initialized) return;
        log.info("Setting com.jayway.jsonpath defaults with {}", configurationDefaults.getClass());
        com.jayway.jsonpath.Configuration.setDefaults(configurationDefaults);
        initialized = true;
    }

    /**
     * Override the default com.jayway.jsonpath configuration (and reset initialization)
     */
    public static void setConfigurationDefaults(com.jayway.jsonpath.Configuration.Defaults configurationDefaults) {
        GsonJsonPathConfigurator.configurationDefaults = configurationDefaults;
        initialized = false;
    }
}
