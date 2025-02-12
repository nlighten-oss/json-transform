package co.nlighten.jsontransform.adapters.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

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
