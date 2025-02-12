package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.adapters.gson.GsonJsonTransformerConfiguration;
import co.nlighten.jsontransform.adapters.jackson.JacksonJsonTransformerConfiguration;
import co.nlighten.jsontransform.adapters.jsonorg.JsonOrgJsonTransformerConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

public class BaseTest {

    protected static JsonAdapter<?,?,?> adapter;
    protected static boolean isJsonOrg;

    @BeforeAll
    static void beforeAll() {
        //JsonTransformerConfiguration.set(new JacksonJsonTransformerConfiguration());
        //JsonTransformerConfiguration.set(new GsonJsonTransformerConfiguration());
        //JsonTransformerConfiguration.set(new JsonOrgJsonTransformerConfiguration());
        adapter = JsonTransformerConfiguration.get().getAdapter();
        isJsonOrg = adapter.getName().equals("jsonorg");
    }

    protected void assertEquals(Object expect, Object result) {
        assertEquals(expect, result, null);
    }
    protected void assertEquals(Object expect, Object result, String message) {
        if (isJsonOrg && adapter.is(expect)) {
            Assertions.assertEquals(adapter.unwrap(expect), adapter.unwrap(result), message);
        } else {
            Assertions.assertEquals(expect, result, message);
        }
    }

    protected Object transform(Object input, Object definition, Map<String, Object> additionalContext) {
        return new JsonTransformer(adapter.wrap(definition)).transform(input, additionalContext);
    }

    protected void assertTransformation(Object input, Object definition, Object expect) {
        assertTransformation(input, definition, adapter.wrap(expect), null);
    }

    protected void assertTransformation(Object input, Object definition, Object expect, Map<String, Object> additionalContext) {
        var result = transform(input, definition, additionalContext);
        var expectType = expect == null ? null : expect.getClass().getTypeName();
        var actualType = result == null ? null : result.getClass().getTypeName();
        var message = "Expected type: <" + expectType + ">, Actual type: <" + actualType + ">";
        if (isJsonOrg && adapter.is(expect)) {
            Assertions.assertEquals(adapter.unwrap(expect), adapter.unwrap(result), message);
        } else {
            Assertions.assertEquals(expect, result, message);
        }
    }

    protected void assertFailTransformation(Object input, Object definition, Object expect) {
        var result = transform(input, definition, null);
        var expectType = expect == null ? null : expect.getClass().getTypeName();
        var actualType = result == null ? null : result.getClass().getTypeName();
        var message = "Expected type: <" + expectType + ">, Actual type: <" + actualType + ">";
        if (isJsonOrg && adapter.is(expect)) {
            Assertions.assertNotEquals(adapter.unwrap(expect), adapter.unwrap(result), message);
        } else {
            Assertions.assertNotEquals(expect, result, message);
        }
    }

    protected Object fromJson(String input) {
        return adapter.unwrap(adapter.parse(input));
    }
}
