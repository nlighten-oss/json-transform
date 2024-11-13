import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionJwtParse", () => {
  test("inline", async () => {
    const testJWT =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    await assertTransformation(testJWT, "$$jwtparse:$", {
      sub: "1234567890",
      name: "John Doe",
      iat: 1516239022,
    });
    //invalid
    await assertTransformation(true, "$$jwtparse:$", null);
  });

  test("object", async () => {
    const testJWT =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    await assertTransformation(
      testJWT,
      {
        $$jwtparse: "$",
      },
      {
        sub: "1234567890",
        name: "John Doe",
        iat: 1516239022,
      },
    );
    //invalid
    await assertTransformation(
      true,
      {
        $$jwtparse: "$",
      },
      null,
    );
  });
});
