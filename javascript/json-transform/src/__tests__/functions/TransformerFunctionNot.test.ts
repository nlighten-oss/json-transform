import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionNot", () => {
  test("inline", async () => {
    await assertTransformation(true, "$$not:$", false);
    await assertTransformation(false, "$$not:$", true);
  });
});
