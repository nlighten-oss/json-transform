package co.nlighten.jsontransform.adapters.jackson;

import co.nlighten.jsontransform.adapters.gson.GsonHelpers;
import co.nlighten.jsontransform.adapters.gson.GsonJsonAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.util.function.Supplier;

public class JacksonHelpers {
    private static ThreadLocal<ObjectMapper> threadSafeMapperBuilder = ThreadLocal.withInitial(JacksonJsonAdapter::mapperBuilder);

    public static ObjectMapper mapper() {
        return threadSafeMapperBuilder.get();
    }

    public static void setObjectMapper(Supplier<ObjectMapper> supplier) {
        JacksonHelpers.threadSafeMapperBuilder = ThreadLocal.withInitial(supplier);
    }
}
