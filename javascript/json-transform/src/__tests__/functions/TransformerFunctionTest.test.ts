import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionTest", () => {
  test("inline", async () => {
    const input = "hello";
    await assertTransformation(input, "$$test([le]):$", true);
    await assertTransformation(input, "$$test(hell):$", true);
    await assertTransformation(input, "$$test(hello$):$", true);
    await assertTransformation(input, "$$test(hell$):$", false);
    await assertTransformation(input, "$$test('^hello$'):$", true);
    await assertTransformation(input, "$$test('^(hello|world)$'):$", true);
    await assertTransformation("HELLO", "$$test('^hello$'):$", false);
    await assertTransformation("HELLO", "$$test('(?i)^hello$'):$", true);
  });
});
