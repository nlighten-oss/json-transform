import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionMatch", () => {
  test("inline", async () => {
    const input = "hello";
    await assertTransformation(input, "$$match([le]):$", "e");
    await assertTransformation(input, "$$match([le]+):$", "ell");
    await assertTransformation(input, "$$match(hell):$", "hell");
    await assertTransformation(input, "$$match(hello$):$", "hello");
    await assertTransformation(input, "$$match(hell$):$", null);
  });

  test("inlineGroup", async () => {
    await assertTransformation("world", "$$match('w(\\\\w+)d',1):$", "orl");
  });
});
