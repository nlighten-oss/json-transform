package co.nlighten.jsontransform.adapters.jsonsmart;

import co.nlighten.jsontransform.adapters.pojo.PojoNull;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.internal.DefaultsImpl;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import com.jayway.jsonpath.spi.json.JsonSmartJsonProvider;
import com.jayway.jsonpath.spi.mapper.JsonOrgMappingProvider;
import com.jayway.jsonpath.spi.mapper.JsonSmartMappingProvider;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

public class JsonSmartHelpers {

    static Configuration getJsonPathConfig() {
        return new Configuration.ConfigurationBuilder()
                .jsonProvider(new JsonSmartJsonProvider())
                .mappingProvider(new JsonSmartMappingProvider())
                .options(Set.of(
                        Option.SUPPRESS_EXCEPTIONS
                ))
                .build();
    }

    private static Stream<Field> getAllFields(Class<?> clazz) {
        if (clazz == null) return Stream.empty();
        return Stream.concat(
                getAllFields(clazz.getSuperclass()),
                Arrays.stream(clazz.getDeclaredFields())
                        .filter(f ->
                                !Modifier.isStatic(f.getModifiers()) && (
                                        Modifier.isPublic(f.getModifiers()) ||
                                                Modifier.isProtected(f.getModifiers())
                                )
                        )
        );
    }

    /**
     * Wraps and unwraps values for JsonSmartJsonAdapter processing
     * @param object object to convert
     * @param unwrap if true, will convert PojoNull to null, otherwise will convert null values to PojoNull
     */
    public static Object convert(Object object, boolean unwrap) {
        if (object == null) {
            return null;
        }
        // number | boolean | string
        if (object instanceof Number ||
                object instanceof Boolean ||
                object instanceof String) {
            return object;
        }
        // special case: char
        if (object instanceof Character) {
            return object.toString();
        }
        // array
        if (object instanceof Iterable<?> i) {
            var result = unwrap ? new ArrayList<>() : new JSONArray();
            for (var item : i) {
                result.add(convert(item, unwrap));
            }
            return result;
        } else if (object.getClass().isArray()) {
            var result = unwrap ? new ArrayList<>() : new JSONArray();
            var length = Array.getLength(object);
            for (var i = 0; i < length; i++) {
                result.add(convert(Array.get(object, i), unwrap));
            }
            return result;
        }
        // object
        var result = unwrap ? new HashMap<String, Object>() : new JSONObject();
        if (object instanceof Map<?, ?> m) {
            // - map
            for (var entry : m.entrySet()) {
                result.put(entry.getKey().toString(), convert(entry.getValue(), unwrap));
            }
        } else {
            // - class type
            getAllFields(object.getClass()).forEach(field -> {
                try {
                    field.setAccessible(true);
                    result.put(field.getName(), convert(field.get(object), unwrap));
                } catch (IllegalAccessException e) {
                    // e.printStackTrace();
                }
            });
        }
        return result;
    }
}
