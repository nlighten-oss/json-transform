package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

public class TransformerFunctionEvalTest extends BaseTest {


    record holder(BigDecimal value) {}
    @Test
    void object() {
        var arr = new Object[] {
                new holder(new BigDecimal(4)),
                new holder( new BigDecimal(2)),
                new holder( new BigDecimal("13.45")),
                Collections.emptyMap()
        };
        assertTransformation(arr, fromJson("""
{
  "$$eval":{
  "$$join":[
  "\\\\$","$avg:",'\\\\$','..value']
  }
}
"""), fromJson("6.483333333333333"));

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
"""), fromJson("4.8625"));



    }

}
