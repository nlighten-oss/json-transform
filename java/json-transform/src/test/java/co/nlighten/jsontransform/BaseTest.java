package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import co.nlighten.jsontransform.adapters.gson.GsonJsonTransformer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

public class BaseTest {

    //protected static JsonAdapter<Object, JSONArray, JSONObject> adapter = JsonOrgJsonTransformer.ADAPTER;
    protected static JsonAdapter<JsonElement, JsonArray, JsonObject> adapter = GsonJsonTransformer.ADAPTER;

    protected Object transform(Object input, Object definition, Map<String, Object> additionalContext) {
        return new GsonJsonTransformer(adapter.wrap(definition)).transform(input, additionalContext);
    }

    protected void assertTransformation(Object input, Object definition, Object expect) {
        assertTransformation(input, definition, adapter.wrap(expect), null);
    }

    protected void assertTransformation(Object input, Object definition, Object expect, Map<String, Object> additionalContext) {
        var result = transform(input, definition, additionalContext);
        var expectType = expect == null ? null : expect.getClass().getTypeName();
        var actualType = result == null ? null : result.getClass().getTypeName();
        Assertions.assertEquals(expect, result, "Expected type: <" + expectType + ">, Actual type: <" + actualType + ">");
    }

    protected void assertFailTransformation(Object input, Object definition, Object expect) {
        var result = transform(input, definition, null);
        var expectType = expect == null ? null : expect.getClass().getTypeName();
        var actualType = result == null ? null : result.getClass().getTypeName();
        Assertions.assertNotEquals(expect, result, "Expected type: <" + expectType + ">, Actual type: <" + actualType + ">");
    }

    protected Object fromJson(String input) {
        return adapter.unwrap(adapter.parse(input), false);
    }
}
