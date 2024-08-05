import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionFind", () => {
  test("findTheTruth", async () => {
    await assertTransformation(
      [0, [], "a"],
      {
        $$find: "$",
        by: "##current",
      },
      "a",
    );

    // explicit boolean (non js style)
    await assertTransformation(
      ["a", "1", "true"],
      {
        $$find: "$",
        by: "$$boolean:##current",
      },
      "true",
    );
  });

  test("nameThatStartsWithB", async () => {
    await assertTransformation(
      [{ name: "alice" }, { name: "ann" }, { name: "Bob" }],
      {
        $$find: "$",
        by: "$$test('(?i)^b'):##current.name",
      },
      { name: "Bob" },
    );
  });

  test("inline", async () => {
    await assertTransformation([0, [], "a"], "$$find(##current):$", "a");
  });
});
