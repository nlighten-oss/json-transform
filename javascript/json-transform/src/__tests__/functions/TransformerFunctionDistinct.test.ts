import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionDistinct", () => {
  test("primitives", async () => {
    const arr = ["a", "b", "c"];
    const arr2 = ["a", "b", "b", "c", "b"];
    await assertTransformation(
      arr2,
      {
        $$distinct: "$",
      },
      arr,
    );

    const mix = ["a", 1, false, "b", "c"];
    const mix2 = ["a", 1, false, "b", "c", "b", 1, false, false];
    await assertTransformation(
      mix2,
      {
        $$distinct: "$",
      },
      mix,
    );

    const withNulls = ["a", null];
    const withNulls2 = ["a", "a", null, null, "a", null];
    await assertTransformation(
      withNulls2,
      {
        $$distinct: "$",
      },
      withNulls,
    );

    const objects = [{ a: 1 }];
    const objects2 = [{ a: 1 }, { a: 1 }, { a: 1 }];
    await assertTransformation(
      objects2,
      {
        $$distinct: "$",
      },
      objects,
    );

    const arrays = [
      ["a", 1],
      ["a", 2],
    ];
    const arrays2 = [
      ["a", 1],
      ["a", 2],
      ["a", 1],
      ["a", 1],
    ];
    await assertTransformation(
      arrays2,
      {
        $$distinct: "$",
      },
      arrays,
    );
  });

  test("withTransformation", async () => {
    const objects = [{ a: 1 }, { a: 1, b: 1 }, { a: 1, b: 2 }, { a: 2, b: 1 }];

    await assertTransformation(
      objects,
      {
        $$distinct: "$",
        by: "##current.a",
      },
      [{ a: 1 }, { a: 2, b: 1 }],
    );

    await assertTransformation(
      objects,
      {
        $$distinct: "$",
        by: "##current.b",
      },
      [{ a: 1 }, { a: 1, b: 1 }, { a: 1, b: 2 }],
    );

    // without by and same arguments should stay the same
    await assertTransformation(
      objects,
      {
        $$distinct: "$",
      },
      objects,
    );
  });

  test("inline", async () => {
    const objects = [{ a: 1 }, { a: 1, b: 1 }, { a: 1, b: 2 }, { a: 2, b: 1 }];
    await assertTransformation(objects, "$$distinct(##current.a):$", [{ a: 1 }, { a: 2, b: 1 }]);
  });
});
