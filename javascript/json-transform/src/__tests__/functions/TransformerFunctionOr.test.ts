import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionOr", () => {
  test("assertOr", async () => {
    const twoOrThree = {
      $$or: [
        { $$is: "$[0]", eq: 2 },
        { $$is: "$[1]", eq: 3 },
      ],
    };
    await assertTransformation([2, 3], twoOrThree, true);
    await assertTransformation([1, 3], twoOrThree, true);
    await assertTransformation([2, 4], twoOrThree, true);
    await assertTransformation([1, 4], twoOrThree, false);
    const firstOrSecond = { $$or: ["$[0]", "$[1]"] };
    await assertTransformation([null, 1], firstOrSecond, true);
    await assertTransformation([null, null], firstOrSecond, false);
  });

  test("inline", async () => {
    await assertTransformation([null, 0], "$$or:$", false);
    await assertTransformation([1, 0], "$$or:$", true);
  });
});
