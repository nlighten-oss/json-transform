package co.nlighten.jsontransform.functions;

import co.nlighten.jsontransform.BaseTest;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class TransformerFunctionUuidTest extends BaseTest {
    @Test
    void formats() {
        var uuid = "a8e41dc6-74c9-42c5-bb03-3bfd623044c5";
        assertTransformation(uuid, "$$uuid:$", uuid);
        assertTransformation(uuid, "$$uuid():$", uuid);
        assertTransformation(uuid, "$$uuid(b36):$", "9zye6dau0hvwo54msqyyjyzt1");
        assertTransformation(uuid, "$$uuid(base36):$", "9zye6dau0hvwo54msqyyjyzt1");
        assertTransformation(uuid, "$$uuid(b62):$", "58gxXh69c8X7f2Id3D84W5");
        assertTransformation(uuid, "$$uuid(Base62):$", "58gxXh69c8X7f2Id3D84W5");
        assertTransformation(uuid, "$$uuid(b64):$", "qOQdxnTJQsW7Azv9YjBExQ");
        assertTransformation(uuid, "$$uuid(base64):$", "qOQdxnTJQsW7Azv9YjBExQ");
        assertTransformation(uuid, "$$uuid('base64'):$", "qOQdxnTJQsW7Azv9YjBExQ");
        assertTransformation(uuid, "$$uuid(N):$", uuid.replace("-", ""));
        assertTransformation(uuid, "$$uuid(no_hyphens):$", uuid.replace("-", ""));
        // from literal
        assertTransformation(null, "$$uuid(n):" + uuid, uuid.replace("-", ""));
    }

    @Test
    void named() {
        // taken from https://uuid.ramsey.dev/en/stable/ examples
        // v3
        assertTransformation("widget/1234567890", "$$uuid(v3,4bdbe8ec-5cb5-11ea-bc55-0242ac130003):$", "53564aa3-4154-3ca5-ac90-dba59dc7d3cb");
        // compare to java's impl
        assertTransformation("widget/1234567890", "$$uuid(v3):$",
                             UUID.nameUUIDFromBytes("widget/1234567890".getBytes(StandardCharsets.UTF_8)).toString());

        // v5
        assertTransformation("widget/1234567890", "$$uuid(v5,4bdbe8ec-5cb5-11ea-bc55-0242ac130003):$", "a35477ae-bfb1-5f2e-b5a4-4711594d855f");
    }
}
