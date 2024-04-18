package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import com.google.gson.JsonNull;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TransformerFunctionMaxTest extends BaseTest {
    @Test
    void inline() {
        var arr = new Object[] {4,-2,13.45, JsonNull.INSTANCE};
        assertTransformation(arr, "$$max($$long:40):$", fromJson("40"));
        assertTransformation(arr, "$$max(-8,NUMBER):$", fromJson("13.45"));
        assertTransformation(arr, "$$max():$", fromJson("13.45"));
        assertTransformation(arr, "$$max(z,STRING):$", fromJson("z"));

    }

    record holder(BigDecimal value) {}
    @Test
    void object() {
        var arr = new holder[] {new holder(new BigDecimal(4)),new holder( new BigDecimal(2)),new holder( new BigDecimal("13.45")), new holder(null)};
        assertTransformation(arr, fromJson("""
{
  "$$max":"$","by":"##current.value","default":"zz","type":"STRING"
}
"""), fromJson("'zz'"));

        assertTransformation(arr, fromJson("""
{
  "$$max":"$","by":"##current.value"
}
"""), fromJson("13.45"));



    }

}
