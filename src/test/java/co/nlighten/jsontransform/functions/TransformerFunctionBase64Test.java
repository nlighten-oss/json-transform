package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TransformerFunctionBase64Test extends BaseTest {

    String t = "hello-world";
    String s = String.join(t, t, t, t, t);
    byte[] b = s.getBytes();
    byte[] b16 = s.getBytes(StandardCharsets.UTF_16);
    String b64 = Base64.getEncoder().encodeToString(b);
    String b64_u16 = Base64.getEncoder().encodeToString(b16);
    String b64_wp = Base64.getEncoder().withoutPadding().encodeToString(b);
    String b64u = Base64.getUrlEncoder().encodeToString(b);
    String b64u_wp = Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    String b64m = Base64.getMimeEncoder().encodeToString(b);
    String b64m_wp = Base64.getMimeEncoder().withoutPadding().encodeToString(b);

    String tu = "hello-unicode-😀👨‍👩‍👧‍👦🪬";
    String tu_b64 = Base64.getEncoder().encodeToString(tu.getBytes(StandardCharsets.UTF_8));
    String tu16_b64 = Base64.getEncoder().encodeToString(tu.getBytes(StandardCharsets.UTF_16));

    @Test
    void encode() {
        assertTransformation(s, "$$base64:$", b64);
        assertTransformation(s, "$$base64():$", b64);
        assertTransformation(s, "$$base64(encode):$", b64);
        assertTransformation(s, "$$base64(enc):$", b64);
        assertTransformation(s, "$$base64(e,Basic):$", b64);
        assertTransformation(s, "$$base64(e,b):$", b64);
        Assertions.assertTrue(b64_u16.length() > b64.length());
        assertTransformation(s, "$$base64(ENCODE,BASIC,false,UTF-16):$", b64_u16);
        assertTransformation(s, "$$base64(e,b,true):$", b64_wp);
        assertTransformation(s, "$$base64(e,url):$", b64u);
        assertTransformation(s, "$$base64(e,u):$", b64u);
        assertTransformation(s, "$$base64(e,u,true):$", b64u_wp);
        assertTransformation(s, "$$base64(E,Mime):$", b64m);
        assertTransformation(s, "$$base64(e,m):$", b64m);
        assertTransformation(s, "$$base64(e,m,true):$", b64m_wp);
    }

    @Test
    void encodeObject() {
        assertTransformation(s, fromJson("{\"$$base64\":\"$\"}"), b64);
        assertTransformation(s, fromJson("{\"$$base64\":\"$\",\"action\":\"encode\"}"), b64);
        assertTransformation(s, fromJson("{\"$$base64\":\"$\",\"action\":\"enc\"}"), b64);
        assertTransformation(s, fromJson("{\"$$base64\":\"$\",\"action\":\"e\",\"rfc\":\"BASIC\"}"), b64);
        assertTransformation(s, fromJson("{\"$$base64\":\"$\",\"action\":\"e\",\"rfc\":\"B\"}"), b64);
        Assertions.assertTrue(b64_u16.length() > b64.length());
        assertTransformation(s, fromJson("{\"$$base64\":\"$\",\"charset\":\"UTF-16\"}"), b64_u16);
        assertTransformation(s, fromJson("{\"$$base64\":\"$\",\"action\":\"e\",\"rfc\":\"B\",\"without_padding\":true}"), b64_wp);
        assertTransformation(s, fromJson("{\"$$base64\":\"$\",\"action\":\"e\",\"rfc\":\"url\"}"), b64u);
        assertTransformation(s, fromJson("{\"$$base64\":\"$\",\"action\":\"e\",\"rfc\":\"u\"}"), b64u);
        assertTransformation(s, fromJson("{\"$$base64\":\"$\",\"action\":\"e\",\"rfc\":\"U\",\"without_padding\":true}"), b64u_wp);
        assertTransformation(s, fromJson("{\"$$base64\":\"$\",\"action\":\"E\",\"rfc\":\"Mime\"}"), b64m);
        assertTransformation(s, fromJson("{\"$$base64\":\"$\",\"action\":\"E\",\"rfc\":\"M\"}"), b64m);
        assertTransformation(s, fromJson("{\"$$base64\":\"$\",\"action\":\"E\",\"rfc\":\"M\",\"without_padding\":true}"), b64m_wp);
    }

    @Test
    void decode() {
        assertTransformation(b64, "$$base64('Decode',b):$", s);
        assertTransformation(b64, "$$base64(decode,b):$", s);
        assertTransformation(b64_wp, "$$base64(d,b):$", s);
        assertTransformation(b64u, "$$base64(d,u):$", s);
        assertTransformation(b64u_wp, "$$base64(d,u):$", s);
        assertTransformation(b64m, "$$base64(d,m):$", s);
        assertTransformation(b64m_wp, "$$base64(d,m):$", s);
        assertTransformation(b64m_wp, "$$base64(d,m):$", s);
        Assertions.assertTrue(tu16_b64.length() > tu_b64.length());
        assertTransformation(tu_b64, "$$base64(d,b,,UTF-8):$", tu);
        assertTransformation(tu16_b64, "$$base64(d,b,,UTF-16):$", tu);
    }

    @Test
    void decodeObject() {
        assertTransformation(b64, fromJson("{\"$$base64\":\"$\",\"action\":\"DECODE\"}"), s);
        assertTransformation(b64_wp, fromJson("{\"$$base64\":\"$\",\"action\":\"DECODE\",\"rfc\":\"b\"}"), s);
        assertTransformation(b64u, fromJson("{\"$$base64\":\"$\",\"action\":\"DECODE\",\"rfc\":\"url\"}"), s);
        assertTransformation(b64u_wp, fromJson("{\"$$base64\":\"$\",\"action\":\"DECODE\",\"rfc\":\"u\"}"), s);
        assertTransformation(b64m, fromJson("{\"$$base64\":\"$\",\"action\":\"DECODE\",\"rfc\":\"mime\"}"), s);
        assertTransformation(b64m_wp, fromJson("{\"$$base64\":\"$\",\"action\":\"DECODE\",\"rfc\":\"m\"}"), s);
        Assertions.assertTrue(tu16_b64.length() > tu_b64.length());
        assertTransformation(tu_b64, fromJson("{\"$$base64\":\"$\",\"action\":\"DECODE\",\"charset\":\"UTF-8\"}"), tu);
        assertTransformation(tu16_b64, fromJson("{\"$$base64\":\"$\",\"action\":\"DECODE\",\"charset\":\"UTF-16\"}"), tu);
    }
}
