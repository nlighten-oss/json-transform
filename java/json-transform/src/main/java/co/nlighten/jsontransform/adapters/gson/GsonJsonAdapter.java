package co.nlighten.jsontransform.adapters.gson;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import com.google.gson.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Supplier;

public class GsonJsonAdapter extends JsonAdapter<JsonElement, JsonArray, JsonObject> {
    public GsonJsonAdapter(Supplier<Gson> gsonSupplier) {
        super(
                GsonObjectAdapter::new,
                GsonArrayAdapter::new,
                GsonHelpers.setFactoryAndReturnJsonPathConfig(gsonSupplier)
        );
    }
    public GsonJsonAdapter() {
        this(null);
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

    private static BigDecimal toBigDecimal(Number number) {
        // try to be as specific as possible in precision
        if (number instanceof BigDecimal bd) {
            return bd;
        }
        if (number instanceof Double || number instanceof Float) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        if (number instanceof BigInteger bi) {
            return new BigDecimal(bi);
        }
        return BigDecimal.valueOf(number.longValue());
    }

    @Override
    public JsonElement wrap(Object value) {
        if (value == null) return JsonNull.INSTANCE;
        if (value instanceof JsonElement je) return je;
        if (value instanceof Character c) return new JsonPrimitive(c);
        if (value instanceof String s) return new JsonPrimitive(s);
        if (value instanceof Boolean b) return new JsonPrimitive(b);
        if (value instanceof Number n) return new JsonPrimitive(toBigDecimal(n));
        return GsonHelpers.GSON().toJsonTree(value);
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
    public String toString(Object value) {
        return GsonHelpers.GSON().toJson(value);
    }
}
