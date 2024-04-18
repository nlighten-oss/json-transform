package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import com.google.gson.JsonNull;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TransformerFunctionSumTest extends BaseTest {
    @Test
    void inline() {
        var arr = new Object[] {4,2,13.45, JsonNull.INSTANCE};
        assertTransformation(arr, "$$sum():$", fromJson("19.45"));
        assertTransformation(arr, "$$sum(1):$", fromJson("20.45"));
    }

    static class holder{
        final BigDecimal value;

        holder(BigDecimal value) {
            this.value = value;
        }
    }
    @Test
    void object() {
        var arr = new holder[] {new holder(new BigDecimal(4)),new holder( new BigDecimal(2)),new holder( new BigDecimal("13.45")), new holder(null)};


        assertTransformation(arr, fromJson("""
{
  "$$sum":"$","by":"##current.value"
}
"""), fromJson("19.45"));

        assertTransformation(arr, fromJson("""
{
  "$$sum":"$","by":"##current.value","default":1
}
"""), fromJson("20.45"));

    }

}
