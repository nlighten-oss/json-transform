package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.adapters.gson.GsonJsonAdapter;
import co.nlighten.jsontransform.adapters.jackson.JacksonJsonAdapter;
import co.nlighten.jsontransform.adapters.jsonorg.JsonOrgJsonAdapter;
import co.nlighten.jsontransform.adapters.jsonsmart.JsonSmartJsonAdapter;
import co.nlighten.jsontransform.adapters.pojo.PojoJsonAdapter;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Stream;

public class MultiAdapterBaseTest {

    private static final Logger log = LoggerFactory.getLogger(MultiAdapterBaseTest.class);

    protected static Stream<JsonAdapter<?,?,?>> provideJsonAdapters() {
        return Stream.of(
                new GsonJsonAdapter(),
                new JacksonJsonAdapter(),
                new JsonOrgJsonAdapter(),
                new JsonSmartJsonAdapter(),
                new PojoJsonAdapter()
        );
    }

    protected void assertEquals(JsonAdapter<?,?,?> adapter, Object expect, Object result) {
        assertEquals(adapter, expect, result, null);
    }
    protected void assertEquals(JsonAdapter<?,?,?> adapter, Object expect, Object result, String message) {
        if (adapter instanceof JsonOrgJsonAdapter && adapter.is(expect)) {
            Assertions.assertEquals(adapter.unwrap(expect), adapter.unwrap(result), message);
        } else {
            Assertions.assertEquals(expect, result, message);
        }
    }

    protected Object transform(JsonAdapter<?,?,?> adapter, Object input, Object definition, Map<String, Object> additionalContext) {
        return new JsonTransformer(definition, adapter).transform(input, additionalContext);
    }

    protected void assertTransformation(JsonAdapter<?,?,?> adapter, Object input, Object definition, Object expect) {
        assertTransformation(adapter, input, definition, adapter.wrap(expect), null);
    }

    protected void assertTransformation(JsonAdapter<?,?,?> adapter, Object input, Object definition, Object expect, Map<String, Object> additionalContext) {
        var result = transform(adapter, input, definition, additionalContext);
        var expectType = expect == null ? null : expect.getClass().getTypeName();
        var actualType = result == null ? null : result.getClass().getTypeName();
        var message = "Expected type: <" + expectType + ">, Actual type: <" + actualType + ">";
        if (!adapter.nodesComparable() && adapter.is(expect)) {
            Assertions.assertEquals(adapter.unwrap(expect), adapter.unwrap(result), message);
        } else {
            Assertions.assertEquals(expect, result, message);
        }
    }

    protected void assertFailTransformation(JsonAdapter<?,?,?> adapter, Object input, Object definition, Object expect) {
        var result = transform(adapter, input, definition, null);
        var expectType = expect == null ? null : expect.getClass().getTypeName();
        var actualType = result == null ? null : result.getClass().getTypeName();
        var message = "Expected type: <" + expectType + ">, Actual type: <" + actualType + ">";
        if (!adapter.nodesComparable() && adapter.is(expect)) {
            Assertions.assertNotEquals(adapter.unwrap(expect), adapter.unwrap(result), message);
        } else {
            Assertions.assertNotEquals(expect, result, message);
        }
    }
}
