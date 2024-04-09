package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionDigestTest extends BaseTest {
    @Test
    void convert() {
        var val = "Hello World";
        var sha1 = "Ck1VqNd45QIvq3AZd8XYQLvEhtA=";
        assertTransformation(val, "$$digest:$", sha1);
        assertTransformation(val, "$$digest(SHA-1):$", sha1);
        assertTransformation(val, "$$digest(SHA-1,BASE64):$", sha1);
        assertTransformation(val, "$$digest(SHA-1,HEX):$",
                             "0a4d55a8d778e5022fab701977c5d840bbc486d0");
        assertTransformation(val, "$$digest(SHA-256):$",
                             "pZGm1Av0IEBKARczz7exkNYsZb8LzaMrV7J32a2fFG4=");
        assertTransformation(val, "$$digest(SHA-256,HEX):$",
                             "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e");
        assertTransformation(val, "$$digest(SHA-384):$",
                             "mVFDKRhrL2rkoTKefubGEKcpY2M1F0rGt0D5AoOW/MgD0Ok4Y6fD2Q+Gvu54L08/");
        assertTransformation(val, "$$digest(SHA-384,BASE64URL):$",
                             "mVFDKRhrL2rkoTKefubGEKcpY2M1F0rGt0D5AoOW_MgD0Ok4Y6fD2Q-Gvu54L08_");
        assertTransformation(val, "$$digest(SHA-384,HEX):$",
                             "99514329186b2f6ae4a1329e7ee6c610a729636335174ac6b740f9028396fcc803d0e93863a7c3d90f86beee782f4f3f");
        assertTransformation(val, "$$digest(SHA-512):$",
                             "LHT9F+2v2A6ER7DUZ0HuJDt+t03SFJoKsbkkb7MDgvJ+hT2FhXGeDmfL2g2qj1FnEGRhXWRa4nrLFb+xRH9Fmw==");
        assertTransformation(val, "$$digest(SHA-512,HEX):$",
                             "2c74fd17edafd80e8447b0d46741ee243b7eb74dd2149a0ab1b9246fb30382f27e853d8585719e0e67cbda0daa8f51671064615d645ae27acb15bfb1447f459b");
        assertTransformation(val, "$$digest(JAVA):$", fromJson("-862545276"));
        assertTransformation(val, "$$digest(MD5):$",
                             "sQqNsWTgdUEFt6mb5y4/5Q==");
        assertTransformation(val, "$$digest(MD5,HEX):$",
                             "b10a8db164e0754105b7a99be72e3fe5");
    }

    @Test
    void javaAsHex() {
        var val = "Hello World";
        assertTransformation(val, "$$numberformat(BASE,16):$$digest(JAVA):$", "-3369657c");

    }
}
