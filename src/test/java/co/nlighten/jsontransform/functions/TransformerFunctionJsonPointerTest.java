package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionJsonPointerTest extends BaseTest {
    @Test
    void get() {
        assertTransformation(fromJson("""
        {
            "b": [
                "c",
                "d",
                {
                    "e": "Hello"
                }
            ]
        }"""), "$$jsonpointer(GET,/b/2/e):$", "Hello");
    }

    @Test
    void getYoDawg() {
        assertTransformation(fromJson("""
        {
            "pointer": "/pointer"
        }"""), "$$jsonpointer(get,$.pointer):$", "/pointer");
    }

    @Test
    void set() {
        assertTransformation(fromJson("""
        {
            "b": [
                "c",
                "d",
                {
                    "e": "Hello"
                }
            ]
        }"""), "$$jsonpointer(SET,/b,'$.b[2]'):$", fromJson("""
        {
            "b": {
                "e": "Hello"
            }
        }"""));
    }
    @Test
    void remove() {
        assertTransformation(fromJson("""
        {
            "b": [
                "c",
                "d",
                {
                    "e": "Hello"
                }
            ]
        }"""), "$$jsonpointer(REMOVE,/b/2):$", fromJson("""
        {
            "b": [
                "c",
                "d"
            ]
        }"""));
    }

}
