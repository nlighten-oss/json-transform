import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionBoolean", () => {
  test("truthy", async () => {
    await assertTransformation(true, "$$boolean:$", true);
    // string
    await assertTransformation("0", "$$boolean(js):$", true);
    await assertTransformation("false", "$$boolean(js):$", true);
    await assertTransformation("true", "$$boolean:$", true);
    await assertTransformation("True", "$$boolean:$", true);
    await assertTransformation("true", "$$boolean(JS):$", true);
    // number
    await assertTransformation(1, "$$boolean:$", true);
    await assertTransformation(-1, "$$boolean:$", true);
    await assertTransformation(BigInt("1"), "$$boolean:$", true);
    // object
    await assertTransformation({ "": 0 }, "$$boolean:$", true);
    // arrays
    await assertTransformation([0], "$$boolean:$", true);
  });

  test("falsy", async () => {
    await assertTransformation(false, "$$boolean:$", false);
    // string
    await assertTransformation("", "$$boolean:$", false);
    await assertTransformation("", "$$boolean(js):$", false);
    await assertTransformation("0", "$$boolean:$", false);
    await assertTransformation("false", "$$boolean:$", false);
    await assertTransformation("False", "$$boolean:$", false);
    // number
    await assertTransformation(0, "$$boolean:$", false);
    await assertTransformation(BigInt(0), "$$boolean:$", false);
    // object
    await assertTransformation(null, "$$boolean:$", false);
    await assertTransformation({}, "$$boolean:$", false);
    // arrays
    await assertTransformation([], "$$boolean:$", false);
  });

  test("object", async () => {
    await assertTransformation("true", JSON.parse('{"$$boolean":"$","style":"JS"}'), true);
    await assertTransformation("false", JSON.parse('{"$$boolean":"$","style":"js"}'), true);
    await assertTransformation("false", JSON.parse('{"$$boolean":"$"}'), false);
  });
});
