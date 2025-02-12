package co.nlighten.jsontransform.adapters.jsonorg;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JsonOrgMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

class JsonOrgJsonPathConfigurator {

    private static final Logger log = LoggerFactory.getLogger(JsonOrgJsonPathConfigurator.class);

    private static boolean initialized = false;

    private static class JaywayJsonOrgConfiguration implements com.jayway.jsonpath.Configuration.Defaults {

        private static final JsonProvider jsonProvider = new JsonOrgJsonProvider();
        private static final Set<Option> options = Set.of(
                Option.SUPPRESS_EXCEPTIONS
        );
        private static final MappingProvider mappingProvider = new JsonOrgMappingProvider();

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

    private static com.jayway.jsonpath.Configuration.Defaults configurationDefaults = new JaywayJsonOrgConfiguration();
    private static com.jayway.jsonpath.Configuration configuration = createConfiguration();

    public static synchronized void setup() {
        if (initialized) return;
        log.info("Setting com.jayway.jsonpath defaults with {}", configurationDefaults.getClass());
        com.jayway.jsonpath.Configuration.setDefaults(configurationDefaults);
        initialized = true;
    }

    /**
     * Override the default com.jayway.jsonpath configuration (and reset initialization)
     */
    public static synchronized void setConfigurationDefaults(com.jayway.jsonpath.Configuration.Defaults defaults) {
        configurationDefaults = defaults;
        configuration = createConfiguration();
        initialized = false;
    }

    public static com.jayway.jsonpath.Configuration createConfiguration() {
        return new Configuration.ConfigurationBuilder().jsonProvider(configurationDefaults.jsonProvider())
                .mappingProvider(configurationDefaults.mappingProvider())
                .options(configurationDefaults.options())
                .build();
    }

    public static com.jayway.jsonpath.Configuration configuration() {
        return configuration;
    }
}
