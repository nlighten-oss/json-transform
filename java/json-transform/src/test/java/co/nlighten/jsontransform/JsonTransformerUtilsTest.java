package co.nlighten.jsontransform;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class JsonTransformerUtilsTest extends BaseTest {
    @Test
    void objects() {
        var result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
{
  "x": "$.input0",
  "b": [
    "$.input1",
    "$.input2"
  ]
}"""));
        Assertions.assertEquals(Map.of(
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
        Assertions.assertEquals(Map.of(
                "$.input0", "$.x",
                "$.input1", "$.b[0]",
                "$.input2", "$.b[1]"
        ), result);
    }

    @Test
    void objects_destruct() {
        var result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
                {
                  "*": "$.input0",
                  "a": "$.input1"
                }"""));
        Assertions.assertEquals(Map.of(
                "$.input0", "$",
                "$.input1", "$.a"
        ), result);
    }

        @Test
    void arrays() {
        var result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
[
  "$.input1",
  "$.input2"
]"""));
        Assertions.assertEquals(Map.of(
                "$.input1", "$[0]",
                "$.input2", "$[1]"
        ), result);

        result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
[
  "$$function(arg1):$.input1",
  "$$function($.input2):const"
]"""));
        Assertions.assertEquals(Map.of(
                "$.input1", "$[0]",
                "$.input2", "$[1]"
        ), result);
    }

    @Test
    void strings() {
        var result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
"$.input0"
"""));
        Assertions.assertEquals(Map.of(
                "$.input0", "$"
        ), result);

        // escaped should be ignored
        result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
"\\\\$.input0"
"""));
        Assertions.assertEquals(Map.of(), result);

        result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
"#now"
"""));
        Assertions.assertEquals(Map.of(
                "#now", "$"
        ), result);

        result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
"$$function(arg1):$.input1"
"""));
        Assertions.assertEquals(Map.of(
                "$.input1", "$"
        ), result);

        result = JsonTransformerUtils.findAllVariableUses(adapter, adapter.parse("""
"$$function($.input2,#now):$.input1"
"""));
        Assertions.assertEquals(Map.of(
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
        Assertions.assertEquals(Map.of(
                "$.parameters.locked$$", "$.filters.locked",
                "$.parameters.x_source", "$.filters.locked"
        ), result);
    }
}
