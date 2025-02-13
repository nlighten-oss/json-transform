package co.nlighten.jsontransform.adapters.pojo;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.internal.DefaultsImpl;

import java.util.Set;

public class PojoHelpers {

    static com.jayway.jsonpath.Configuration getJsonPathConfig() {
        // the default implementation is bundled with the library
        // thus not requiring additional dependencies
        var defaults = DefaultsImpl.INSTANCE;
        return new Configuration.ConfigurationBuilder()
                .jsonProvider(defaults.jsonProvider())
                .mappingProvider(defaults.mappingProvider())
                .options(Set.of(
                        Option.SUPPRESS_EXCEPTIONS
                ))
                .build();
    }

    static Object parse(String value) {
        return DefaultsImpl.INSTANCE.jsonProvider().parse(value);
    }

    static String toJson(Object value) {
        return DefaultsImpl.INSTANCE.jsonProvider().toJson(value);
    }
}
