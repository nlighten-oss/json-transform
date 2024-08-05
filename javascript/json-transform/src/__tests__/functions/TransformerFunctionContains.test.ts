import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionContains", () => {
  test("object", async () => {
    await assertTransformation(
      [0, [], "a"],
      {
        $$contains: "$",
        that: "a",
      },
      true,
    );
    // with transformation
    await assertTransformation(
      "a",
      {
        $$contains: ["b", "$"],
        that: "a",
      },
      true,
    );

    await assertTransformation(
      [0, [], "a"],
      {
        $$contains: "$",
        that: "b",
      },
      false,
    );
  });

  test("inline", async () => {
    await assertTransformation([0, [], "a"], "$$contains(a):$", true);
    await assertTransformation([0, [], "a"], "$$contains(b):$", false);
  });
});
