package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

public class TransformerFunctionUriParseTest extends BaseTest {
    @Test
    void parse() {
        assertTransformation("https://user:pass@example.com:9090/whatever/?q=a&q=b#fragment", "$$uriparse:$", fromJson("""
{
    "scheme": "https",
    "user_info": "user:pass",
    "authority": "user:pass@example.com:9090",
    "host": "example.com:9090",
    "hostname": "example.com",
    "port": 9090,
    "path": "/whatever/",
    "query": {
        "q": "a",
        "q$$": [ "a", "b" ]
    },
    "query_raw": "q=a&q=b",
    "fragment": "fragment"
}
"""));

        assertTransformation("https://example.com/whatever", "$$uriparse:$", fromJson("""
{
    "scheme": "https",
    "authority": "example.com",
    "host": "example.com",
    "hostname": "example.com",
    "path": "/whatever"
}
"""));
    }

    @Test
    void badSyntax() {
        var input = "<https///>";
        assertTransformation(input, "$$uriparse:$", null);
    }
}
