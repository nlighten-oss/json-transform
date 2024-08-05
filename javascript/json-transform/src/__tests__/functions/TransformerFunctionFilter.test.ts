import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionFilter", () => {
  test("truthyValuesOnly", async () => {
    await assertTransformation(
      ["a", true, "false", 0, 1, [], [0]],
      {
        $$filter: "$",
        by: "##current",
      },
      ["a", true, "false", 1, [0]],
    );

    // explicit boolean (non js style)
    await assertTransformation(
      ["a", true, "false", 0, 1, [], [0]],
      {
        $$filter: "$",
        by: "$$boolean:##current",
      },
      [true, 1, [0]],
    );
  });
  test("namesThatStartsWithA", async () => {
    await assertTransformation(
      [{ name: "alice" }, { name: "ann" }, { name: "bob" }],
      {
        $$filter: "$",
        by: "$$test(^a):##current.name",
      },
      [{ name: "alice" }, { name: "ann" }],
    );
  });

  test("inline", async () => {
    const expected = ["a", true, "false", 1, [0]];
    await assertTransformation(["a", true, "false", 0, 1, [], [0]], "$$filter(##current):$", expected);
  });
});
