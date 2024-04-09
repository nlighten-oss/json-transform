package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionJwtParseTest extends BaseTest {

    @Test
    void inline() {
        var testJWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        assertTransformation(testJWT, "$$jwtparse:$", fromJson("""
{
  "sub": "1234567890",
  "name": "John Doe",
  "iat": 1516239022
}"""));
        //invalid
        assertTransformation(true, "$$jwtparse:$", null);
    }

    @Test
    void object() {
        var testJWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        assertTransformation(testJWT, fromJson("""
{
  "$$jwtparse": "$"
}"""), fromJson("""
{
  "sub": "1234567890",
  "name": "John Doe",
  "iat": 1516239022
}"""));
        //invalid
        assertTransformation(true, fromJson("""
{
  "$$jwtparse": "$"
}"""), null);
    }

}
