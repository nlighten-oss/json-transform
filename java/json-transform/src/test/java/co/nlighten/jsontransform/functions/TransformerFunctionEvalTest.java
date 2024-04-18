package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TransformerFunctionEvalTest extends BaseTest {


    record holder(BigDecimal value) {}
    @Test
    void object() {
        var arr = new holder[] {new holder(new BigDecimal(4)),new holder( new BigDecimal(2)),new holder( new BigDecimal("13.45")), new holder(null)};
        assertTransformation(arr, fromJson("""
{
  "$$eval":{
  "$$join":[
  "\\\\$","$avg:",'\\\\$','..value']
  }
}
"""), fromJson("4.86"));

        assertTransformation(arr, fromJson("""
{
  "$$eval":{"$$jsonparse":{
  "$$join":[
  "{",
  "'$$avg'",
  ":","'$'",
  ", 'by':'##current.value'",
  "}"
    ]
  }
}}
"""), fromJson("4.86"));



    }

}
