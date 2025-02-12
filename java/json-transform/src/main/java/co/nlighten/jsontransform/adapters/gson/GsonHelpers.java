package co.nlighten.jsontransform.adapters.gson;

import com.google.gson.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Supplier;

public class GsonHelpers {
    private static ThreadLocal<Gson> threadSafeGsonBuilder = ThreadLocal.withInitial(() -> GsonJsonAdapter.gsonBuilder().create());

    public static Gson GSON() {
        return threadSafeGsonBuilder.get();
    }

    public static void setGson(Supplier<Gson> supplier) {
        GsonHelpers.threadSafeGsonBuilder = ThreadLocal.withInitial(supplier);
        GsonJsonPathConfigurator.setConfigurationDefaults(new GsonJsonPathConfigurator.JaywayGSONConfiguration(GSON()));
    }

    public static JsonElement wrap(final Object value) {
        if (value instanceof JsonElement je) return je;
        if (value instanceof Character c) return new JsonPrimitive(c);
        if (value instanceof String s) return new JsonPrimitive(s);
        if (value instanceof Boolean b) return new JsonPrimitive(b);
        if (value instanceof Number n) return new JsonPrimitive(toBigDecimal(n));
        return GSON().toJsonTree(value);
    }

    public static BigDecimal toBigDecimal(Number number) {
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

}
