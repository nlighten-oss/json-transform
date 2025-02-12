package co.nlighten.jsontransform.adapters.pojo;

import java.lang.reflect.Array;
import java.util.*;

public class PojoMapper {
    public static Object convert(Object object, boolean unwrap) {
        if (object == null || object instanceof PojoNull) {
            return unwrap ? null : PojoNull.INSTANCE;
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
                result.add(convert(item, unwrap));
            }
            return result;
        } else if (object.getClass().isArray()) {
            var result = new ArrayList<>();
            var length = Array.getLength(object);
            for (var i = 0; i < length; i++) {
                result.add(convert(Array.get(object, i), unwrap));
            }
            return result;
        }
        // object
        var result = new HashMap<String, Object>();
        if (object instanceof Map<?, ?> m) {
            // - map
            for (var entry : m.entrySet()) {
                result.put(entry.getKey().toString(), convert(entry.getValue(), unwrap));
            }
        } else {
            // - class type
            var fields = object.getClass().getDeclaredFields();
            for (var field : fields) {
                try {
                    field.setAccessible(true);
                    result.put(field.getName(), convert(field.get(object), unwrap));
                } catch (IllegalAccessException e) {
                    // e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static Object deepClone(Object value) {
        return convert(value, false);
    }
}
