import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionIsNull", () => {
  test("nullTest", async () => {
    await assertTransformation(null, "$$isnull:$", true);
    await assertTransformation(undefined, "$$isnull:$", true);
    await assertTransformation(0, "$$isnull():$", false);
    await assertTransformation("", "$$isnull:$", false);
    await assertTransformation(false, "$$isnull:$", false);
  });
});
