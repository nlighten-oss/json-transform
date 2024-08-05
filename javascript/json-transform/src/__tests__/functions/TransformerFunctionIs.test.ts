import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionIs", () => {
  test("assertEq_NotEq", async () => {
    await assertTransformation("A", { $$is: "$", eq: "A" }, true);
    await assertTransformation("A", { $$is: "$", eq: "B" }, false);
    await assertTransformation(4, { $$is: "$", eq: 4 }, true);
    await assertTransformation(4.5, { $$is: "$", eq: 4 }, false);
    await assertTransformation(4.5, { $$is: "$", neq: 4 }, true);
    await assertTransformation(4.5, { $$is: "$", eq: 4.5, neq: 4 }, true);
  });

  test("assertGt_Gte_Lt_Lte", async () => {
    await assertTransformation("B", { $$is: "$", gt: "A" }, true);
    await assertTransformation("B", { $$is: "$", gte: "B" }, true);
    await assertTransformation(4, { $$is: "$", gt: 3 }, true);
    await assertTransformation(4, { $$is: "$", gte: 4 }, true);
    await assertTransformation(4, { $$is: "$", lte: 4 }, true);
    await assertTransformation(3, { $$is: "$", lt: 4 }, true);
    await assertTransformation(4, { $$is: "$", lt: 4 }, false);
    await assertTransformation([1, 2, 3], { $$is: "$", lt: [true, "a", "b", "c"] }, true);
    await assertTransformation([1, 2, 3], { $$is: "$", gte: ["a", "b", "c"] }, true);
    await assertTransformation({ a: 1, b: 2 }, { $$is: "$", gte: { key1: "a", key2: "b" } }, true);
  });

  test("assertIn_NotIn", async () => {
    await assertTransformation("A", { $$is: "$", in: ["A", "B"] }, true);

    await assertTransformation(["A", "B"], { $$is: "A", in: "$" }, true);
    await assertTransformation(["A", "B"], { $$is: "B", in: ["$[0]", "$[1]"] }, true);
    await assertTransformation(["a", "B"], { $$is: "A", in: "$" }, false);
    // other types
    await assertTransformation([false, true], { $$is: true, in: "$" }, true);
    await assertTransformation(null, { $$is: 30, in: [10, 20, 30] }, true);
    await assertTransformation(null, { $$is: 30, nin: [10, 20, 30] }, false);
    await assertTransformation(null, { $$is: 30, in: "$" }, false);
    await assertTransformation(null, { $$is: 30, nin: "$" }, false);
    // even complex
    await assertTransformation(null, { $$is: [{ a: 1 }], in: [[{ a: 4 }], [{ a: 1 }], [{ a: 3 }]] }, true);
    await assertTransformation(null, { $$is: 30, in: [10, 20, 30], nin: [40, 50, 60] }, true);
    await assertTransformation(null, { $$is: 30, in: [40, 50, 60], nin: [10, 20, 30] }, false);
  });

  test("andOrExample", async () => {
    // check if number is between 1 < x < 3 or 4 <= x <= 6
    var between1And3 = { $$is: "$", gt: 1, lt: 3 };
    var between4And6 = { $$is: "$", gte: 4, lte: 6 };
    var definition = { $$is: true, in: [between1And3, between4And6] };

    var goodValues = [2, 4, 5, 6];
    var badValues = [1, 3, 7];

    for (const value of goodValues) {
      await assertTransformation(value, definition, true);
    }
    for (const value of badValues) {
      await assertTransformation(value, definition, false);
    }
  });

  test("objectOpThat", async () => {
    await assertTransformation("A", { $$is: "$", op: "EQ", that: "A" }, true);
    await assertTransformation("A", { $$is: "$", op: "EQ", that: "B" }, false);
    await assertTransformation("A", { $$is: "$", op: "!=", that: "B" }, true);
    await assertTransformation(5, { $$is: "$", op: ">", that: 2 }, true);
  });

  test("inlineOpThat", async () => {
    await assertTransformation("A", "$$is(EQ,A):$", true);
    await assertTransformation("A", "$$is(=,B):$", false);
    await assertTransformation("A", "$$is(!=,B):$", true);
    // string comparison vs number comparison
    await assertTransformation("10", "$$is(>,2):$", false);
    await assertTransformation(10, "$$is(>,2):$", true);
    // in / not in
    await assertTransformation(["a", "b", "A", "B"], "$$is(IN,$):A", true);
    await assertTransformation(["a", "b", "A", "B"], "$$is(IN,$):C", false);
    await assertTransformation(["a", "b", "A", "B"], "$$is(NIN,$):C", true);
    await assertTransformation(null, "$$is(in,$):C", false);
    await assertTransformation(null, "$$is(Nin,$):C", false);
  });

  test("inlineCompareToNull", async () => {
    await assertTransformation(null, "$$is(!=,#null):$", false);
    await assertTransformation(null, "$$is(=,#null):$", true);
  });
});
