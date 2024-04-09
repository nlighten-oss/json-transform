package co.nlighten.jsontransform.adapters.jsonorg;

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
                Option.SUPPRESS_EXCEPTIONS,
                Option.DEFAULT_PATH_LEAF_TO_NULL
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
        JsonOrgJsonPathConfigurator.configurationDefaults = configurationDefaults;
        initialized = false;
    }
}
