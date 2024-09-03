import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionMatchAll", () => {
  test("inline", async () => {
    const input = "hello my helloKitty";
    await assertTransformation(input, "$$matchall([el]):$", ["e", "l", "l", "e", "l", "l"]);
    await assertTransformation(input, "$$matchall([le]+):$", ["ell", "ell"]);
    await assertTransformation(input, "$$matchall(hell):$", ["hell", "hell"]);
    await assertTransformation(input, "$$matchall(^hello):$", ["hello"]);
    await assertTransformation(input, "$$matchall(hello$):$", null);
  });

  test("inlineGroup", async () => {
    await assertTransformation("world to waterWorld", "$$matchall('w(\\\\w+)d',1):$", ["orl", "aterWorl"]);
  });
});
