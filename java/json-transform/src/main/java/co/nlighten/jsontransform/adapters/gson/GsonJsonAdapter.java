package co.nlighten.jsontransform.adapters.gson;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import com.google.gson.*;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.math.BigDecimal;

public class GsonJsonAdapter extends JsonAdapter<JsonElement, JsonArray, JsonObject> {

    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static GsonBuilder gsonBuilder() {
        return new GsonBuilder()
                .setDateFormat(ISO_DATETIME_FORMAT)
                .setNumberToNumberStrategy(ToNumberPolicy.BIG_DECIMAL)
                .setObjectToNumberStrategy(ToNumberPolicy.BIG_DECIMAL);
    }

    public GsonJsonAdapter() {
        super(JsonElement.class, GsonObjectAdapter::new, GsonArrayAdapter::new);
    }

    @Override
    public boolean is(Object value) {
        return value instanceof JsonElement;
    }

    @Override
    public boolean isJsonString(Object value) {
        return value instanceof JsonPrimitive jp && jp.isString();
    }

    @Override
    public boolean isJsonNumber(Object value) {
        return value instanceof JsonPrimitive jp && jp.isNumber();
    }

    @Override
    public boolean isJsonBoolean(Object value) {
        return value instanceof JsonPrimitive jp && jp.isBoolean();
    }

    @Override
    public boolean isNull(Object value) {
        return value == null || value instanceof JsonNull;
    }

    @Override
    public JsonElement jsonNull() {
        return JsonNull.INSTANCE;
    }

    @Override
    public JsonElement wrap(Object value) {
        return GsonHelpers.wrap(value);
    }

    @Override
    public Object unwrap(Object value, boolean reduceBigDecimals) {
        return GsonJsonElementUnwrapper.unwrap(value, false, reduceBigDecimals);
    }

    @Override
    public JsonElement parse(String value) {
        return GsonHelpers.GSON().fromJson(value, JsonElement.class);
    }

    @Override
    public Object clone(Object value) {
        return ((JsonElement)value).deepCopy();
    }

    @Override
    public Number getNumber(Object value) {
        return ((JsonElement)value).getAsNumber();
    }

    @Override
    public BigDecimal getNumberAsBigDecimal(Object value) {
        return ((JsonElement)value).getAsBigDecimal();
    }

    @Override
    public Boolean getBoolean(Object value) {
        return ((JsonElement)value).getAsBoolean();
    }

    @Override
    public void setupJsonPath() {
        GsonJsonPathConfigurator.setup();
    }

    @Override
    public DocumentContext getDocumentContext(Object payload) {
        GsonJsonPathConfigurator.setup();
        Object document = payload;
        if (!(payload instanceof JsonElement)) {
            document = wrap(payload);
        }
        return JsonPath.parse(document);
    }
    @Override
    public String toString(Object value) {
        return GsonHelpers.GSON().toJson(value);
    }
}
