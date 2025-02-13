package co.nlighten.jsontransform;

import co.nlighten.jsontransform.adapters.JsonAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;

public class JsonTransformerUtilsTest extends MultiAdapterBaseTest {
    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void objects(JsonAdapter<?,?,?> adapter) {
        var result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
{
  "x": "$.input0",
  "b": [
    "$.input1",
    "$.input2"
  ]
}"""));
        assertEquals(adapter, Map.of(
                "$.input0", "$.x",
                "$.input1", "$.b[0]",
                "$.input2", "$.b[1]"
        ), result);

        result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
{
  "x": "$$func:$.input0",
  "b": [
    "$$function(arg1):$.input1",
    "$$function($.input2):const"
  ]
}"""));
        assertEquals(adapter, Map.of(
                "$.input0", "$.x",
                "$.input1", "$.b[0]",
                "$.input2", "$.b[1]"
        ), result);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void objects_destruct(JsonAdapter<?,?,?> adapter) {
        var result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
                {
                  "*": "$.input0",
                  "a": "$.input1"
                }"""));
        assertEquals(adapter, Map.of(
                "$.input0", "$",
                "$.input1", "$.a"
        ), result);
    }

        @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void arrays(JsonAdapter<?,?,?> adapter) {
        var result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
[
  "$.input1",
  "$.input2"
]"""));
        assertEquals(adapter, Map.of(
                "$.input1", "$[0]",
                "$.input2", "$[1]"
        ), result);

        result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
[
  "$$function(arg1):$.input1",
  "$$function($.input2):const"
]"""));
        assertEquals(adapter, Map.of(
                "$.input1", "$[0]",
                "$.input2", "$[1]"
        ), result);
    }

    @ParameterizedTest()
    @MethodSource("co.nlighten.jsontransform.MultiAdapterBaseTest#provideJsonAdapters")
    void strings(JsonAdapter<?,?,?> adapter) {
        var result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
"$.input0"
"""));
        assertEquals(adapter, Map.of(
                "$.input0", "$"
        ), result);

        // escaped should be ignored
        result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
"\\\\$.input0"
"""));
        assertEquals(adapter, Map.of(), result);

        result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
"#now"
"""));
        assertEquals(adapter, Map.of(
                "#now", "$"
        ), result);

        result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
"$$function(arg1):$.input1"
"""));
        assertEquals(adapter, Map.of(
                "$.input1", "$"
        ), result);

        result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
"$$function($.input2,#now):$.input1"
"""));
        assertEquals(adapter, Map.of(
                "$.input1", "$",
                "$.input2", "$",
                "#now", "$"
        ), result);

        result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
                {
                  "filters": {
                    "locked": {
                      "$$map": "$.parameters.locked$$",
                      "to": "$$boolean:##current",
                      "x_target": "$.parameters.x_source"
                    }
                  }
                }
"""));
        assertEquals(adapter, Map.of(
                "$.parameters.locked$$", "$.filters.locked",
                "$.parameters.x_source", "$.filters.locked"
        ), result);
    }
}
