package co.nlighten.jsontransform.adapters.jackson;

import co.nlighten.jsontransform.adapters.JsonAdapterHelpers;
import co.nlighten.jsontransform.adapters.JsonArrayAdapter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JacksonArrayAdapter extends JsonArrayAdapter<JsonNode, ArrayNode, ObjectNode> {

    @Override
    public ArrayNode create() {
        return JsonNodeFactory.instance.arrayNode();
    }

    @Override
    public ArrayNode create(int capacity) {
        return JsonNodeFactory.instance.arrayNode(capacity);
    }

    @Override
    public void add(ArrayNode array, String value) {
        array.add(value);
    }

    @Override
    public void add(ArrayNode array, Number value) {
        if (value instanceof Short) {
            array.add(value.shortValue());
        } else if (value instanceof Integer) {
            array.add(value.intValue());
        } else if (value instanceof Long) {
            array.add(value.longValue());
        } else if (value instanceof Double) {
            array.add(value.doubleValue());
        } else if (value instanceof Float) {
            array.add(value.floatValue());
        } else if (value instanceof BigDecimal bd) {
            array.add(bd);
        } else if (value instanceof BigInteger bi) {
            array.add(bi);
        } else {
            array.add(value.toString());
        }
    }

    @Override
    public void add(ArrayNode array, Boolean value) {
        array.add(value);
    }

    @Override
    public void add(ArrayNode array, Character value) {
        array.add(value);
    }

    @Override
    public void add(ArrayNode array, JsonNode value) {
        array.add(value);
    }

    @Override
    public void add(ArrayNode array, ArrayNode value) {
        array.add(value);
    }

    @Override
    public void set(ArrayNode array, int index, JsonNode value) {
        if (array.size() > index || JsonAdapterHelpers.trySetArrayAtOOB(this, array, index, value, NullNode.getInstance())) {
            array.set(index, value);
        }
    }

    @Override
    public JsonNode remove(ArrayNode array, int index) {
        return array.remove(index);
    }

    @Override
    public JsonNode get(ArrayNode array, int index) {
        return array.get(index);
    }

    @Override
    public int size(ArrayNode array) {
        return array.size();
    }

    @Override
    public boolean is(Object value) {
        return value instanceof ArrayNode;
    }

    @Override
    public Stream<JsonNode> stream(ArrayNode array, boolean parallel) {
        return StreamSupport.stream(array.spliterator(), parallel);
    }
}
