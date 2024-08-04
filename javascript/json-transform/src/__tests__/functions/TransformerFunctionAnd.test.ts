import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionAnd", () => {
  test("assertAnd", async () => {
    const twoAndThree = {
      $$and: [
        { $$is: "$[0]", eq: 2 },
        { $$is: "$[1]", eq: 3 },
      ],
    };
    await assertTransformation([2, 3], twoAndThree, true);
    await assertTransformation([2, 4], twoAndThree, false);
  });

  test("inline", async () => {
    await assertTransformation([null, 0], "$$and:$", false);
    await assertTransformation([1, 0], "$$and:$", false);
    await assertTransformation([1, true], "$$and:$", true);
  });
});
