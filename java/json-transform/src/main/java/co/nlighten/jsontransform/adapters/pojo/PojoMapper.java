package co.nlighten.jsontransform.adapters.pojo;

import co.nlighten.jsontransform.adapters.JsonAdapterHelpers;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

public class PojoMapper {

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
     * Wraps and unwraps values for PojoJsonAdapter processing
     * @param object object to convert
     * @param unwrap if true, will convert PojoNull to null, otherwise will convert null values to PojoNull
     * @param reduceBigDecimals if true, will convert BigDecimal to smaller number structures if possible
     */
    public static Object convert(Object object, boolean unwrap, boolean reduceBigDecimals) {
        if (object == null || object instanceof PojoNull) {
            return unwrap ? null : PojoNull.INSTANCE;
        }
        // number
        if (object instanceof Number n) {
            return JsonAdapterHelpers.unwrapNumber(n, reduceBigDecimals);
        }
        // boolean | string
        if (object instanceof Boolean ||
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
                result.add(convert(item, unwrap, reduceBigDecimals));
            }
            return result;
        } else if (object.getClass().isArray()) {
            var result = new ArrayList<>();
            var length = Array.getLength(object);
            for (var i = 0; i < length; i++) {
                result.add(convert(Array.get(object, i), unwrap, reduceBigDecimals));
            }
            return result;
        }
        // object
        var result = new HashMap<String, Object>();
        if (object instanceof Map<?, ?> m) {
            // - map
            for (var entry : m.entrySet()) {
                result.put(entry.getKey().toString(), convert(entry.getValue(), unwrap, reduceBigDecimals));
            }
        } else {
            // - class type
            getAllFields(object.getClass()).forEach(field -> {
                try {
                    field.setAccessible(true);
                    result.put(field.getName(), convert(field.get(object), unwrap, reduceBigDecimals));
                } catch (IllegalAccessException e) {
                    // e.printStackTrace();
                }
            });
        }
        return result;
    }
    public static Object convert(Object object) {
        return convert(object, false, false);
    }

    public static Object deepClone(Object value) {
        return convert(value);
    }
}
