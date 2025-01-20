package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import com.google.gson.JsonNull;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TransformerFunctionMinTest extends BaseTest {
    @Test
    void inline() {
        var arr = new Object[] {4,-2,13.45, JsonNull.INSTANCE};
        assertTransformation(arr, "$$min($$long:-4):$", fromJson("-4"));
        assertTransformation(arr, "$$min(-8,NUMBER):$", fromJson("-8"));
        assertTransformation(arr, "$$min():$", fromJson(null));
        assertTransformation(arr, "$$min(z,STRING):$", fromJson("-2"));

    }

    record holder(BigDecimal value) {}
    @Test
    void object() {
        var arr = new holder[] {new holder(new BigDecimal(4)),new holder( new BigDecimal(2)),new holder( new BigDecimal("13.45")), new holder(null)};


        assertTransformation(arr, fromJson("""
{
  "$$min":"$","by":"##current.value"
}
"""), fromJson("null"));

        assertTransformation(arr, fromJson("""
{
  "$$min":"$","by":"##current.value","default":1
}
"""), fromJson("1"));

    }

}
