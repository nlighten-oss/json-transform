import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionFlat", () => {
  test("object", async () => {
    const arr = ["a", "b", "c"];
    const arr2 = ["d", "e", "f"];
    const arr3 = [arr, arr2];
    const flatCombined = arr3.flatMap(x => x);
    const flatArr = arr.slice();
    await assertTransformation(
      arr3,
      {
        $$flat: ["$[0]", "$[1]"],
      },
      flatCombined,
    );
    await assertTransformation(
      arr3,
      {
        $$flat: ["$[0]", "$.pointingToNowhere"],
      },
      flatArr,
    );
    await assertTransformation(
      arr3,
      {
        $$flat: [
          ["a", "b", "c"],
          ["d", "e", "f"],
        ],
      },
      flatCombined,
    );
    await assertTransformation(
      arr,
      {
        $$flat: [["a", "b", "c"], []],
      },
      flatArr,
    );
    await assertTransformation(
      arr,
      {
        $$flat: [["a", "b", "c"], null],
      },
      flatArr,
    );
  });
});
