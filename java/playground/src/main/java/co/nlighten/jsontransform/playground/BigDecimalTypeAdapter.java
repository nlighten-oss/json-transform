package co.nlighten.jsontransform.playground;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;

public class BigDecimalTypeAdapter implements JsonSerializer<BigDecimal>, JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement instanceof JsonPrimitive jp && jp.isString() && jp.getAsString().startsWith("bd#")) {
            return new BigDecimal(jp.getAsString().substring(3));
        }
        return null;
    }

    @Override
    public JsonElement serialize(BigDecimal bigDecimal, Type type, JsonSerializationContext jsonSerializationContext) {
        if (bigDecimal == null) {
            return JsonNull.INSTANCE;
        }
        return new JsonPrimitive("bd#" + bigDecimal);
    }
}