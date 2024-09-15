import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionObject", () => {
  test("inline", async () => {
    await assertTransformation(
      [
        ["a", 1],
        ["b", true],
        ["c", "C"],
      ],
      "$$object:$",
      {
        a: 1,
        b: true,
        c: "C",
      },
    );
    await assertTransformation(
      [
        [0, 1],
        [1, true],
        [2, "C"],
      ],
      "$$object:$",
      { 0: 1, 1: true, 2: "C" },
    );

    // invalid input will yield an empty object
    await assertTransformation(null, "$$object", {});
    await assertTransformation(0.5, "$$object:$", {});
    await assertTransformation("test", "$$object:$", {});
    await assertTransformation(false, "$$object:$", {});
  });

  test("object", async () => {
    await assertTransformation(
      [
        ["a", 1],
        ["b", true],
        ["c", "C"],
      ],

      { $$object: "$" },
      {
        a: 1,
        b: true,
        c: "C",
      },
    );
    await assertTransformation(
      [
        [0, 1],
        [1, true],
        [2, "C"],
      ],

      { $$object: "$" },
      { 0: 1, 1: true, 2: "C" },
    );
    await assertTransformation(null, { $$object: "$" }, {});
    await assertTransformation(0.5, { $$object: "$" }, {});
    await assertTransformation(false, { $$object: "$" }, {});
    // explicit

    await assertTransformation(
      {
        key: 1,
        value: true,
      },
      { $$object: [["$.key", "$.value"]] },
      { 1: true },
    );

    await assertTransformation(
      {
        key: "a",
        value: 0.5,
      },
      { $$object: [["$.key", "$.value"]] },
      { a: 0.5 },
    );
  });
});
