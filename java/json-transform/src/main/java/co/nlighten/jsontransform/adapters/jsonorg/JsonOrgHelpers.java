package co.nlighten.jsontransform.adapters.jsonorg;

import co.nlighten.jsontransform.adapters.JsonAdapterHelpers;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JsonOrgJsonProvider;
import com.jayway.jsonpath.spi.mapper.JsonOrgMappingProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

public class JsonOrgHelpers {

    static com.jayway.jsonpath.Configuration getJsonPathConfig() {
        return new Configuration.ConfigurationBuilder()
                .jsonProvider(new JsonOrgJsonProvider())
                .mappingProvider(new JsonOrgMappingProvider())
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

    public static Object simplifyBeforeWrap(Object object) {
        if (object instanceof JSONObject ||
            object instanceof JSONArray ||
            object instanceof JSONString ||
            object == JSONObject.NULL) {
            return object;
        }
        if (object == null) {
            return JSONObject.NULL;
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
            var result = new ArrayList<>();
            for (var item : i) {
                result.add(simplifyBeforeWrap(item));
            }
            return result;
        } else if (object.getClass().isArray()) {
            var result = new ArrayList<>();
            var length = Array.getLength(object);
            for (var i = 0; i < length; i++) {
                result.add(simplifyBeforeWrap(Array.get(object, i)));
            }
            return result;
        }
        // object
        var result = new HashMap<String, Object>();
        if (object instanceof Map<?, ?> m) {
            // - map
            for (var entry : m.entrySet()) {
                result.put(entry.getKey().toString(), simplifyBeforeWrap(entry.getValue()));
            }
        } else {
            // - class type
            getAllFields(object.getClass()).forEach(field -> {
                try {
                    field.setAccessible(true);
                    result.put(field.getName(), simplifyBeforeWrap(field.get(object)));
                } catch (IllegalAccessException e) {
                    // e.printStackTrace();
                }
            });
        }
        return result;
    }

    public static int getContentsHashCode(Object object) {
        if (object == JSONObject.NULL || object == null)
            return 0;
        if (object instanceof JSONObject jo) {
            return jo.toString().hashCode();
        }
        if (object instanceof JSONArray ja) {
            return ja.toString().hashCode();
        }
        if (object instanceof JSONString js) {
            return js.toString().hashCode();
        }
        return object.hashCode();
    }

    static Map<String, Object> unwrapObject(JSONObject value, boolean reduceBigDecimals) {
        var obj = new HashMap<String, Object>();
        for (var key : value.keySet()) {
            obj.put(key, unwrap(value.get(key), reduceBigDecimals));
        }
        return obj;
    }

    static List<Object> unwrapArray(JSONArray value, boolean reduceBigDecimals) {
        var results = new ArrayList<>(value.length());
        for (var item : value) {
            results.add(unwrap(item, reduceBigDecimals));
        }
        return results;
    }

    static Object unwrap(Object value, boolean reduceBigDecimals) {
        if (JSONObject.NULL.equals(value)) {
            return null;
        }
        if (value instanceof JSONObject jo) {
            return unwrapObject(jo, reduceBigDecimals);
        }
        if (value instanceof JSONArray ja) {
            return unwrapArray(ja, reduceBigDecimals);
        }
        if (value instanceof JSONString ja) {
            return ja.toString();
        }
        if (value instanceof Number n) {
            return JsonAdapterHelpers.unwrapNumber(n, reduceBigDecimals);
        }
        return value;
    }
}
