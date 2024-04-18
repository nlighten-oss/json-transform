package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import com.google.gson.JsonNull;
import org.junit.jupiter.api.Test;

public class TransformerFunctionIsNullTest extends BaseTest {
    @Test
    void nullTest() {
        assertTransformation(null, "$$isnull:$", true);
        assertTransformation(JsonNull.INSTANCE, "$$isnull:$", true);
        assertTransformation(0, "$$isnull():$", false);
        assertTransformation("", "$$isnull:$", false);
        assertTransformation(false, "$$isnull:$", false);
    }
}
