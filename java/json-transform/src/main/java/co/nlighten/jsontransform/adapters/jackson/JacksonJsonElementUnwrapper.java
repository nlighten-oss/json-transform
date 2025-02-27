package co.nlighten.jsontransform.adapters.jackson;

import co.nlighten.jsontransform.adapters.JsonAdapterHelpers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JacksonJsonElementUnwrapper {

    public static Object unwrapJsonPrimitive(final ValueNode p) {
        return unwrapJsonPrimitive(p, false);
    }
    public static Object unwrapJsonPrimitive(final ValueNode p, final boolean reduceBigDecimals) {
        if (p instanceof TextNode) {
            return p.asText();
        } else if (p.isBoolean()) {
            return p.asBoolean();
        } else if (p.isNumber()) {
            return JsonAdapterHelpers.unwrapNumber(p.numberValue(), reduceBigDecimals);
        }
        throw new RuntimeException("Invalid JsonPrimitive type: " + p);
    }

    public static List<Object> unwrapJsonArray(ArrayNode ja) {
        return unwrapJsonArray(ja, false);
    }
    public static List<Object> unwrapJsonArray(ArrayNode ja, boolean reduceBigDecimals) {
        var arr = new ArrayList<>();
        ja.forEach(el -> arr.add(unwrap(el, false, reduceBigDecimals)));
        return arr;
    }

    public static Map<String, Object> unwrapJsonObject(ObjectNode jo) {
        return unwrapJsonObject(jo, false);
    }
    public static Map<String, Object> unwrapJsonObject(ObjectNode jo, boolean reduceBigDecimals) {
        var obj = new HashMap<String, Object>();
        for (var kv : jo.properties()) {
            obj.put(kv.getKey(), unwrap(kv.getValue(), false, reduceBigDecimals));
        }
        return obj;
    }

    /**
     * Unwrap a JsonElement to standard java types
     * @param o suspected JsonElement object to unwrap
     * @param onlyPrimitives convert only if JsonPrimitive
     * @param reduceBigDecimals reduce BigDecimal to Integer, Long or Double if possible
     */
    public static Object unwrap(final Object o, boolean onlyPrimitives, boolean reduceBigDecimals) {
        if (!(o instanceof JsonNode e)) {
            return o;
        }
        if (e.isNull() || e.isMissingNode()) {
            return null;
        }
        if (e instanceof ValueNode p) {
            return unwrapJsonPrimitive(p, reduceBigDecimals);
        }
        if (onlyPrimitives) return o;
        // not null or primitive, so must be an object or an array
        if (e instanceof ArrayNode a) {
            return unwrapJsonArray(a, reduceBigDecimals);
        }
        return unwrapJsonObject((ObjectNode)e, reduceBigDecimals);
    }

    public static Object unwrap(final Object o, boolean onlyPrimitives) {
        return unwrap(o, onlyPrimitives, false);
    }

    public static Object unwrap(final Object o) {
        return unwrap(o, false, false);
    }
}
