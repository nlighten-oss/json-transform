package co.nlighten.jsontransform.adapters.jackson;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.adapters.JsonAdapterHelpers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;

import java.math.BigDecimal;
import java.util.function.Supplier;

public class JacksonJsonAdapter extends JsonAdapter<JsonNode, ArrayNode, ObjectNode> {
    public JacksonJsonAdapter(Supplier<ObjectMapper> jacksonSupplier) {
        super(
                JacksonObjectAdapter::new,
                JacksonArrayAdapter::new,
                JacksonHelpers.setFactoryAndReturnJsonPathConfig(jacksonSupplier)
        );
    }
    public JacksonJsonAdapter() {
        this(null);
    }

    @Override
    public boolean is(Object value) {
        return value instanceof JsonNode;
    }

    @Override
    public boolean isJsonString(Object value) {
        return value instanceof TextNode;
    }

    @Override
    public boolean isJsonNumber(Object value) {
        return value instanceof NumericNode;
    }

    @Override
    public boolean isJsonBoolean(Object value) {
        return value instanceof BooleanNode;
    }

    @Override
    public boolean isNull(Object value) {
        return value == null || value instanceof MissingNode || value instanceof NullNode;
    }

    @Override
    public JsonNode jsonNull() {
        return NullNode.getInstance();
    }

    @Override
    public JsonNode wrap(Object value) {
        return JacksonHelpers.mapper().valueToTree(value);
    }

    @Override
    public Object unwrap(Object value, boolean reduceBigDecimals) {
        return JacksonJsonElementUnwrapper.unwrap(value, false, reduceBigDecimals);
    }

    @Override
    public JsonNode parse(String value) {
        var provider = jsonPathConfiguration.jsonProvider();
        if (value != null && value.startsWith("'") && value.endsWith("'") && value.length() >= 2) {
            return (JsonNode) provider.parse(
                JsonAdapterHelpers.singleQuotedStringToDoubleQuoted(value)
            );
        }
        return (JsonNode) provider.parse(value);
    }

    @Override
    public Object clone(Object value) {
        return ((JsonNode)value).deepCopy();
    }

    @Override
    public Number getNumber(Object value) {
        if (value instanceof NumericNode n) {
            return n.numberValue();
        }
        if (value instanceof TextNode t) {
            return new BigDecimal(t.asText());
        }
        return null;
    }

    @Override
    public BigDecimal getNumberAsBigDecimal(Object value) {
        if (value instanceof NumericNode n) {
            return n.decimalValue();
        }
        if (value instanceof TextNode t) {
            return new BigDecimal(t.asText());
        }
        return null;
    }

    @Override
    public Boolean getBoolean(Object value) {
        return ((JsonNode)value).asBoolean();
    }

    @Override
    public String toString(Object value) {
        var mapper = jsonPathConfiguration.mappingProvider();
        var provider = jsonPathConfiguration.jsonProvider();
        return provider.toJson(mapper.map(value, JsonNode.class, jsonPathConfiguration));
    }
}
