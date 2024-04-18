package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TransformerFunctionUrlDecodeTest extends BaseTest {

    String u = "hello+unicode+😀👨‍👩‍👧‍👦🪬";
    String uu8 = URLEncoder.encode(u, StandardCharsets.UTF_8);
    String uu16 = URLEncoder.encode(u, StandardCharsets.UTF_16);

    @Test
    void decode() {
        var input = "not+url%2Bsafe%3F%3Dx%26b%3Dpath%2Fpath";
        var decoded = "not url+safe?=x&b=path/path";
        assertTransformation(input, "$$urldecode:$", decoded);
        assertTransformation(input, "$$urldecode():$", decoded);
        assertTransformation(null, "$$urldecode:" + input, decoded);
        Assertions.assertTrue(uu16.length() > uu8.length());
        assertTransformation(uu8, "$$urldecode(UTF-8):$", u);
        assertTransformation(uu16, "$$urldecode(UTF-16):$", u);
    }
}
