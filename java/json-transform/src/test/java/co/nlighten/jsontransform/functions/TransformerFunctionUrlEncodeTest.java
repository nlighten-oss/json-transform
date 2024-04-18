package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TransformerFunctionUrlEncodeTest extends BaseTest {

    String u = "hello+unicode+😀👨‍👩‍👧‍👦🪬";
    String uu8 = URLEncoder.encode(u, StandardCharsets.UTF_8);
    String uu16 = URLEncoder.encode(u, StandardCharsets.UTF_16);

    @Test
    void encode() {
        var input = "not url+safe?=x&b=path/path";
        var encoded = "not+url%2Bsafe%3F%3Dx%26b%3Dpath%2Fpath";
        assertTransformation(input, "$$urlencode:$", encoded);
        assertTransformation(input, "$$urlencode():$", encoded);
        assertTransformation(null, "$$urlencode:" + input, encoded);
        Assertions.assertTrue(uu16.length() > uu8.length());
        assertTransformation(u, "$$urlencode(UTF-8):$", uu8);
        assertTransformation(u, "$$urlencode(UTF-16):$", uu16);
    }
}
