package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import co.nlighten.jsontransform.adapters.gson.GsonJsonTransformer;
import co.nlighten.jsontransform.functions.common.FunctionContext;
import co.nlighten.jsontransform.functions.common.TransformerFunction;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class TransformerFunctionSwitchTest extends BaseTest {
    @Test
    void object() {
        assertTransformation("B",fromJson("""
{
  "$$switch": "$",
  "cases": { "a": 1, "B": 2, "c": 3 }
}
"""),fromJson("2"));
        // no default
        assertTransformation("D",fromJson("""
{
  "$$switch": "$",
  "cases": { "a": 1, "B": 2, "c": 3 }
}
"""), null);
        // with default
        assertTransformation("D",fromJson("""
{
  "$$switch": "$",
  "cases": { "a": 1, "B": 2, "c": 3 },
  "default": 4
}
"""), fromJson("4"));
        // full extraction
        assertTransformation(fromJson("""
[
    "B",
    { "a": 1, "B": 2, "c": 3 }
  ]"""),fromJson("""
{
  "$$switch": "$[0]", "cases": "$[1]"
}
"""),fromJson("2"));
    }

    @Test
    void inline() {
        assertTransformation(fromJson("{ \"a\": 1, \"B\": 2, \"c\": 3 }"),
                             "$$switch($,'$$decimal:4'):B", fromJson("2"));
        assertTransformation(fromJson("{ \"a\": 1, \"B\": 2, \"c\": 3 }"),
                             "$$switch($,'$$decimal:4'):C", fromJson("4"));
    }


    @Test
    void lazyEval() {
        var transformer = fromJson("""
{
  "$$switch": "$",
  "cases": {
    "a": "$$switchLazyEval:1",
    "B": "$$switchLazyEval:2",
    "c": "$$switchLazyEval:3"
  }
}
""");

        var callCount = new AtomicInteger();
        GsonJsonTransformer.FUNCTIONS.registerFunctions(Map.entry("switchLazyEval", new TransformerFunction<>(GsonJsonTransformer.ADAPTER) {
            @Override
            public Object apply(FunctionContext<JsonElement, JsonArray, JsonObject> context) {
                callCount.incrementAndGet();
                return context.get(null);
            }
        }));

        callCount.set(0);
        assertTransformation("B", transformer, "2");
        // assert that "c" was called twice (check A, check B, break)
        Assertions.assertEquals(1, callCount.get());

        callCount.set(0);
        assertTransformation("c", transformer, "3");
        // assert that "c" was called once (check A, break)
        Assertions.assertEquals(1, callCount.get());
    }
}
