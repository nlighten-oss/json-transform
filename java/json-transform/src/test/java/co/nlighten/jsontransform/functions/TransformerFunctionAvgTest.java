package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import com.google.gson.JsonNull;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TransformerFunctionAvgTest extends BaseTest {
    @Test
    void inline() {
        var arr = new Object[] {4,2,13.45, JsonNull.INSTANCE};
        assertTransformation(arr, "$$avg():$", fromJson("4.86"));
        assertTransformation(arr, "$$avg(1):$", fromJson("5.11"));
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
  "$$avg":"$","by":"##current.value"
}
"""), fromJson("4.86"));

        assertTransformation(arr, fromJson("""
{
  "$$avg":"$","by":"##current.value","default":1
}
"""), fromJson("5.11"));

    }

}
