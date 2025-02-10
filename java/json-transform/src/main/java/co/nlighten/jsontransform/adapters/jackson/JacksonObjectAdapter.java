package co.nlighten.jsontransform.adapters.jackson;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.adapters.JsonObjectAdapter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JacksonObjectAdapter extends JsonObjectAdapter<JsonNode, ArrayNode, ObjectNode> {

    public JacksonObjectAdapter(JsonAdapter<JsonNode, ArrayNode, ObjectNode> adapter) {
        super(ObjectNode.class, adapter);
    }

    @Override
    public ObjectNode create() {
        return JsonNodeFactory.instance.objectNode();
    }

    @Override
    public void add(ObjectNode object, String key, String value) {
        object.put(key, value);
    }

    @Override
    public void add(ObjectNode object, String key, Number value) {
        if (value instanceof Short) {
            object.put(key, value.shortValue());
        } else if (value instanceof Integer) {
            object.put(key, value.intValue());
        } else if (value instanceof Long) {
            object.put(key, value.longValue());
        } else if (value instanceof Double) {
            object.put(key, value.doubleValue());
        } else if (value instanceof Float) {
            object.put(key, value.floatValue());
        } else if (value instanceof BigDecimal bd) {
            object.put(key, bd);
        } else if (value instanceof BigInteger bi) {
            object.put(key, bi);
        } else {
            object.put(key, value.toString());
        }
    }

    @Override
    public void add(ObjectNode object, String key, Boolean value) {
        object.put(key, value);
    }

    @Override
    public void add(ObjectNode object, String key, Character value) {
        object.put(key, value);
    }

    @Override
    public void add(ObjectNode object, String key, JsonNode value) {
        object.set(key, value);
    }

    @Override
    public void add(ObjectNode object, String key, ArrayNode value) {
        object.set(key, value);
    }

    @Override
    public JsonNode remove(ObjectNode object, String key) {
        return object.remove(key);
    }

    @Override
    public boolean has(ObjectNode object, String key) {
        return object.has(key);
    }

    @Override
    public JsonNode get(ObjectNode object, String key) {
        return object.get(key);
    }

    @Override
    public int size(ObjectNode object) {
        return object.size();
    }

    @Override
    public boolean is(Object value) {
        return value instanceof ObjectNode;
    }

    @Override
    public ObjectNode convert(Object value) {
        return (ObjectNode) value;
    }

    @Override
    public Set<Map.Entry<String, JsonNode>> entrySet(ObjectNode object) {
        return object.properties();
    }

    @Override
    public Set<String> keySet(ObjectNode object) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(
                        object.fieldNames(), 0), false)
                .collect(Collectors.toSet());
    }
}
