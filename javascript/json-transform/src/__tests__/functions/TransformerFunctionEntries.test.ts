import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionEntries", () => {
  test("inline", async () => {
    await assertTransformation(
      {
        a: 1,
        b: true,
        c: "C",
      },
      "$$entries:$",
      [
        ["a", 1],
        ["b", true],
        ["c", "C"],
      ],
    );
    await assertTransformation([1, true, "C"], "$$entries:$", [
      [0, 1],
      [1, true],
      [2, "C"],
    ]);
  });

  test("object", async () => {
    await assertTransformation(
      {
        a: 1,
        b: true,
        c: "C",
      },
      { $$entries: "$" },
      [
        ["a", 1],
        ["b", true],
        ["c", "C"],
      ],
    );

    await assertTransformation([1, true, "C"], { $$entries: "$" }, [
      [0, 1],
      [1, true],
      [2, "C"],
    ]);

    // explicit

    await assertTransformation(
      {
        a: 1,
        b: true,
        c: "C",
      },
      { $$entries: { "*": "$", d: 0.5 } },
      [
        ["a", 1],
        ["b", true],
        ["c", "C"],
        ["d", 0.5],
      ],
    );
  });
});
