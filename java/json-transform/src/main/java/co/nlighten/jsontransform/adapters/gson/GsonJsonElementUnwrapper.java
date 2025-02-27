package co.nlighten.jsontransform.adapters.gson;

import co.nlighten.jsontransform.adapters.JsonAdapterHelpers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GsonJsonElementUnwrapper {

    public static Object unwrapJsonPrimitive(final JsonPrimitive p) {
        return unwrapJsonPrimitive(p, false);
    }
    public static Object unwrapJsonPrimitive(final JsonPrimitive p, final boolean reduceBigDecimals) {
        if (p.isString()) {
            return p.getAsString();
        } else if (p.isBoolean()) {
            return p.getAsBoolean();
        } else if (p.isNumber()) {
            return JsonAdapterHelpers.unwrapNumber(p.getAsNumber(), reduceBigDecimals);
        }
        throw new RuntimeException("Invalid JsonPrimitive type: " + p);
    }

    public static List<Object> unwrapJsonArray(JsonArray ja) {
        return unwrapJsonArray(ja, false);
    }
    public static List<Object> unwrapJsonArray(JsonArray ja, boolean reduceBigDecimals) {
        var arr = new ArrayList<>();
        ja.forEach(el -> arr.add(unwrap(el, false, reduceBigDecimals)));
        return arr;
    }

    public static Map<String, Object> unwrapJsonObject(JsonObject jo) {
        return unwrapJsonObject(jo, false);
    }
    public static Map<String, Object> unwrapJsonObject(JsonObject jo, boolean reduceBigDecimals) {
        var obj = new HashMap<String, Object>();
        jo.entrySet().forEach(kv -> {
            obj.put(kv.getKey(), unwrap(kv.getValue(), false, reduceBigDecimals));
        });
        return obj;
    }

    /**
     * Unwrap a JsonElement to standard java types
     * @param o suspected JsonElement object to unwrap
     * @param onlyPrimitives convert only if JsonPrimitive
     * @param reduceBigDecimals reduce BigDecimal to Integer, Long or Double if possible
     */
    public static Object unwrap(final Object o, boolean onlyPrimitives, boolean reduceBigDecimals) {
        if (!(o instanceof JsonElement e)) {
            return o;
        }
        if (e.isJsonNull()) {
            return null;
        }
        if (e instanceof JsonPrimitive p) {
            return unwrapJsonPrimitive(p, reduceBigDecimals);
        }
        if (onlyPrimitives) return o;
        // not null or primitive, so must be an object or an array
        if (e instanceof JsonArray a) {
            return unwrapJsonArray(a, reduceBigDecimals);
        }
        return unwrapJsonObject(e.getAsJsonObject(), reduceBigDecimals);
    }

    public static Object unwrap(final Object o, boolean onlyPrimitives) {
       return unwrap(o, onlyPrimitives, false);
    }

    public static Object unwrap(final Object o) {
        return unwrap(o, false, false);
    }
}
