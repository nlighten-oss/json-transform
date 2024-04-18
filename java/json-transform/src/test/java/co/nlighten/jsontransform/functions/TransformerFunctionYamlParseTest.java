package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import co.nlighten.jsontransform.formats.yaml.YamlFormatTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionYamlParseTest extends BaseTest {
    @Test
    void inline() {
        assertTransformation(YamlFormatTest.YAML, "$$yamlparse:$", fromJson(YamlFormatTest.JSON));
    }

    @Test
    void object() {
        assertTransformation(YamlFormatTest.YAML, fromJson("""
        { "$$yamlparse": "$" }"""), fromJson(YamlFormatTest.JSON));
    }
}
