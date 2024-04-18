package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class TransformerFunctionMathTest extends BaseTest {
    @Test
    void inline() {
        var arr = new BigDecimal[] {new BigDecimal(4), new BigDecimal(2)};
        assertTransformation(arr, "$$math(+,$[0],$[1])", fromJson("6"));
        assertTransformation(arr, "$$math($[0],+,$[1])", fromJson("6"));
        assertTransformation(arr, "$$math($[0],+)", arr[0]);
        assertTransformation(arr, "$$math($[0],-,$[1])", fromJson("2"));
        assertTransformation(arr, "$$math($[0],*,$[1])", fromJson("8"));
        assertTransformation(arr, "$$math($[0],/,$[1])", fromJson("2"));
        assertTransformation(arr, "$$math($[0],//,3)", fromJson("1"));
        assertTransformation(arr, "$$math($[0],%,3)", fromJson("1"));
        assertTransformation(arr, "$$math($[1],^,3)", fromJson("8"));
        assertTransformation(arr, "$$math(3,&,1)", fromJson("1"));
        assertTransformation(arr, "$$math(6,AND,3)", fromJson("2"));
        assertTransformation(arr, "$$math(6,|,3)", fromJson("7"));
        assertTransformation(arr, "$$math(6,~,3)", fromJson("5"));
        assertTransformation(arr, "$$math(6,>>,1)", fromJson("3"));
        assertTransformation(arr, "$$math(6,<<,3)", fromJson("48"));
        assertTransformation(arr, "$$math(MIN,$[0],$[1])", fromJson("2"));
        assertTransformation(arr, "$$math(MAX,$[0],$[1])", fromJson("4"));
        assertTransformation(arr, "$$math(SQRT,81)", fromJson("9"));
        assertTransformation(arr, "$$math(SQRT):81", fromJson("9"));
        assertTransformation(arr, "$$math(SQRT):$$math($[0],^,2)", arr[0]);
        assertTransformation(arr, "$$math(ROUND,4.6)", fromJson("5"));
        assertTransformation(arr, "$$math(ROUND):4.6", fromJson("5"));
        assertTransformation(arr, "$$math(ROUND,1):4.66", fromJson("4.7"));
        assertTransformation(arr, "$$math(ROUND,4.66,1)", fromJson("4.7"));
        assertTransformation(arr, "$$math(4.66,ROUND,1)", fromJson("4.7"));
        assertTransformation(arr, "$$math(FLOOR,4.6)", fromJson("4"));
        assertTransformation(arr, "$$math(FLOOR):4.6", fromJson("4"));
        assertTransformation(arr, "$$math(FLOOR,1):4.66", fromJson("4.6"));
        assertTransformation(arr, "$$math(CEIL,4.2)", fromJson("5"));
        assertTransformation(arr, "$$math(CEIL):4.2", fromJson("5"));
        assertTransformation(arr, "$$math(CEIL,1):4.22", fromJson("4.3"));
        assertTransformation(arr, "$$math(ABS,-10)", fromJson("10"));
        assertTransformation(arr, "$$math(ABS):-10", fromJson("10"));
        assertTransformation(arr, "$$math(NEG,$[0])", fromJson("-4"));
        assertTransformation(arr, "$$math(NEG):$[0]", fromJson("-4"));
        assertTransformation(arr, "$$math(SIG):42", fromJson("1"));
        assertTransformation(arr, "$$math(SIGNUM):-42", fromJson("-1"));
        assertTransformation(arr, "$$math(SIG):0", fromJson("0"));
    }

    @Test
    void object() {
        var arr = new BigDecimal[] {new BigDecimal(4), new BigDecimal(2)};
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "+", "$[0]", "$[1]" ]
}
"""), fromJson("6"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "$[0]", "+", "$[1]" ]
}
"""), fromJson("6"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "-", "$[0]", "$[1]" ]
}
"""), fromJson("2"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "*", "$[0]", "$[1]" ]
}
"""), fromJson("8"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "/", "$[0]", "$[1]" ]
}
"""), fromJson("2"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "//", "$[0]", "3" ]
}
"""), fromJson("1"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "$[0]", "//", 3 ]
}
"""), fromJson("1"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "%", "$[0]", "3" ]
}
"""), fromJson("1"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "^", "$[1]", "3" ]
}
"""), fromJson("8"));

        assertTransformation(arr, fromJson("""
{ "$$math": [ 3, "&", 1 ] }"""), fromJson("1"));
        assertTransformation(arr, fromJson("""
{ "$$math": [ 6, "&", 3 ] }"""), fromJson("2"));
        assertTransformation(arr, fromJson("""
{ "$$math": [ 6, "|", 3 ] }"""), fromJson("7"));
        assertTransformation(arr, fromJson("""
{ "$$math": [ 6, "XOR", 3 ] }"""), fromJson("5"));
        assertTransformation(arr, fromJson("""
{ "$$math": [ 6, ">>", 1 ] }"""), fromJson("3"));
        assertTransformation(arr, fromJson("""
{ "$$math": [ 6, "<<", 3 ] }"""), fromJson("48"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "sqrt", 81 ]
}
"""), fromJson("9"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "min", "$[0]", "$[1]" ]
}
"""), fromJson("2"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "max", "$[0]", "$[1]" ]
}
"""), fromJson("4"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "round", 4.6 ]
}
"""), fromJson("5"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "round", 4.66, 1 ]
}
"""), fromJson("4.7"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "floor", 4.6 ]
}
"""), fromJson("4"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "floor", 4.66, 1 ]
}
"""), fromJson("4.6"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "ceil", 4.2 ]
}
"""), fromJson("5"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "ceil", 4.22, 1 ]
}
"""), fromJson("4.3"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "abs", -10 ]
}
"""), fromJson("10"));
        assertTransformation(arr, fromJson("""
{
  "$$math": [ "neg", "$[0]" ]
}
"""), fromJson("-4"));
        assertTransformation(fromJson("[\"abs\",-10]"), fromJson("""
{
  "$$math": "$"
}
"""), fromJson("10"));

        assertTransformation(arr, fromJson("""
{ "$$math": [ "SIG", 42 ] }"""), fromJson("1"));
        assertTransformation(arr, fromJson("""
{ "$$math": [ "SIGNUM", -42 ] }"""), fromJson("-1"));
        assertTransformation(arr, fromJson("""
{ "$$math": [ "SIG", 0 ] }"""), fromJson("0"));

    }

    @Test
    void combineScaling() {
        assertTransformation(null, "$$decimal(2):$$math(1,*,0.987654321)", fromJson("0.99"));
        assertTransformation(null, "$$decimal(2,FLOOR):$$math(1,*,0.987654321)", fromJson("0.98"));
    }
}
