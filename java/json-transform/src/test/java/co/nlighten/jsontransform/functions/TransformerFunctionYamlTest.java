package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import co.nlighten.jsontransform.adapters.gson.GsonHelpers;
import co.nlighten.jsontransform.formats.yaml.YamlFormat;
import co.nlighten.jsontransform.formats.yaml.YamlFormatTest;
import com.google.gson.JsonElement;
import org.junit.jupiter.api.Test;

public class TransformerFunctionYamlTest extends BaseTest {
    YamlFormat yamlFormat = new YamlFormat(adapter);

    @Test
    void inline() {
        var jsonElement = GsonHelpers.GSON().fromJson(YamlFormatTest.JSON, JsonElement.class);
        assertTransformation(jsonElement, "$$yaml:$", yamlFormat.serialize(jsonElement));
    }

    @Test
    void object() {
        var jsonElement = GsonHelpers.GSON().fromJson(YamlFormatTest.JSON, JsonElement.class);
        assertTransformation(jsonElement, fromJson("""
        { "$$yaml": "$" }"""), yamlFormat.serialize(jsonElement));
    }
}
