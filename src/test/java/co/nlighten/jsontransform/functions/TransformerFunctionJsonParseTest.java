package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import co.nlighten.jsontransform.adapters.gson.GsonHelpers;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TransformerFunctionJsonParseTest extends BaseTest {

    Gson gson = GsonHelpers.GSON();

    @Test
    void fromString() {
        var strVal = "text";
        assertTransformation(gson.toJson(strVal), "$$jsonparse:$", strVal);
        assertTransformation(gson.toJson(strVal), "$$jsonparse():$", strVal);
    }
    @Test
    void fromBoolean() {
        var boolVal = true;
        assertTransformation(gson.toJson(boolVal), "$$jsonparse:$", boolVal);
    }
    @Test
    void fromNumber() {
        var numVal = 123;
        var str = gson.toJson(numVal);
        assertTransformation(str, "$$jsonparse:$", gson.fromJson(str, Object.class));
    }

    @Test
    void fromBigDecimal() {
        var numVal = new BigDecimal("1234567890.098765432123456789");
        var numStr = gson.toJson(numVal);
        assertTransformation(numStr, "$$jsonparse:$", gson.fromJson(numStr, Object.class));
        var bigVal = new BigDecimal("123456789123456789123456789123456789");
        var bigStr = gson.toJson(bigVal);
        assertTransformation(bigStr, "$$jsonparse:$", gson.fromJson(bigStr, Object.class));
    }

    @Test
    void fromObject() {
        var jsonString = "{\"a\":\"b\"}";
        var jsonAsObj = fromJson(jsonString);
        assertTransformation(jsonString, "$$jsonparse:$", jsonAsObj);
    }
    @Test
    void fromArray() {
        var jsonString = "[\"a\",\"b\"]";
        var jsonAsObj = fromJson(jsonString);
        assertTransformation(jsonString, "$$jsonparse:$", jsonAsObj);
    }

    @Test
    void objectFromObject() {
        var jsonString = "{\"a\":\"b\"}";
        var jsonAsObj = fromJson(jsonString);
        assertTransformation(jsonString, fromJson("""
{
"$$jsonparse": "$"
}"""), jsonAsObj);

    }
}
