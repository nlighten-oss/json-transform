package co.nlighten.jsontransform.adapters.jackson;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.JsonNodeFeature;
import com.fasterxml.jackson.databind.node.*;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

public class JacksonJsonAdapter extends JsonAdapter<JsonNode, ArrayNode, ObjectNode> {

    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static ObjectMapper mapperBuilder() {
        return new ObjectMapper()
                .setDateFormat(new SimpleDateFormat(ISO_DATETIME_FORMAT))
                .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
                .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
                .configure(JsonNodeFeature.STRIP_TRAILING_BIGDECIMAL_ZEROES, true);
    }

    public JacksonJsonAdapter() {
        super(JsonNode.class, JacksonObjectAdapter::new, JacksonArrayAdapter::new);
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

    private static final String BACKSLASH = "\\";
    private String singleQuotedStringToDoubleQuoted(String value) {
        return "\"" +
                value.substring(1, value.length() - 1)
                .replace("\"", BACKSLASH + "\"")
                .replace(BACKSLASH + "'", "'") +
                "\"";
    }

    @Override
    public JsonNode parse(String value) {
        try {
            if (value != null && value.startsWith("'") && value.endsWith("'") && value.length() > 2) {
                return JacksonHelpers.mapper().readValue(
                        singleQuotedStringToDoubleQuoted(value), JsonNode.class);
            }
            return JacksonHelpers.mapper().readValue(value, JsonNode.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
    public void setupJsonPath() {
        JacksonJsonPathConfigurator.setup();
    }

    @Override
    public DocumentContext getDocumentContext(Object payload) {
        JacksonJsonPathConfigurator.setup();
        Object document = payload;
        if (!(payload instanceof JsonNode)) {
            document = wrap(payload);
        }
        return JsonPath.parse(document);
    }
    @Override
    public String toString(Object value) {
        try {
            return JacksonHelpers.mapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
