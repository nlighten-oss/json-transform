import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionIf", () => {
  test("arrayForm", async () => {
    await assertTransformation(
      null,
      {
        $$if: [true, "b", "c"],
      },
      "b",
    );
    await assertTransformation(
      null,
      {
        $$if: [true, "b"],
      },
      "b",
    );
    await assertTransformation(
      null,
      {
        $$if: [false, "b"],
      },
      null,
    );
    await assertTransformation(
      null,
      {
        $$if: [null, "b"],
      },
      null,
    );

    var arr = ["a", "b", "c"];
    // $[0] && $[1] -> "d"
    await assertTransformation(
      arr,
      {
        $$if: [
          "$[0]",
          {
            $$if: ["$[1]", "d"],
          },
        ],
      },
      "d",
    );

    // $[0] || $[1] || $[2] -> "d"
    await assertTransformation(
      [false, 0, "true"],
      {
        $$if: ["$[0]", "d", { $$if: ["$[1]", "d", { $$if: ["$[2]", "d"] }] }],
      },
      "d",
    );

    var arr2 = ["a", "b", "c"];
    await assertTransformation(
      arr2,
      {
        $$if: ["$[0]", "$[1]", "$[2]"],
      },
      arr2[1],
    );

    var arr3 = [null, "b", "c"];
    await assertTransformation(
      arr3,
      {
        $$if: ["$[0]", "$[1]", "$[2]"],
      },
      arr3[2],
    );

    var arr4 = ["false", "b", "c"];
    await assertTransformation(
      arr4,
      {
        $$if: ["$$boolean:$[0]", "$[1]", "$[2]"],
      },
      arr4[2],
    );

    // invalid input - treat as object
    await assertTransformation([true], { $$if: "$" }, null);
    await assertTransformation("hello", { $$if: "$" }, null);
  });

  test("objectForm", async () => {
    await assertTransformation(
      null,
      {
        $$if: true,
        then: "b",
        else: "c",
      },
      "b",
    );
    await assertTransformation(
      null,
      {
        $$if: true,
        then: "b",
      },
      "b",
    );
    await assertTransformation(
      null,
      {
        $$if: false,
        then: "b",
      },
      null,
    );
    await assertTransformation(
      null,
      {
        $$if: "$",
        then: "b",
      },
      null,
    );

    var arr = ["a", "b", "c"];
    // $[0] && $[1] -> "d"
    await assertTransformation(
      arr,
      {
        $$if: "$[0]",
        then: { $$if: "$[1]", then: "d" },
      },
      "d",
    );

    // $[0] || $[1] || $[2] -> "d"
    await assertTransformation(
      [false, 0, "true"],
      {
        $$if: "$[0]",
        then: "d",
        else: {
          $$if: "$[1]",
          then: "d",
          else: {
            $$if: "$[2]",
            then: "d",
          },
        },
      },
      "d",
    );

    var arr2 = ["a", "b", "c"];
    await assertTransformation(
      arr2,
      {
        $$if: "$[0]",
        then: "$[1]",
        else: "$[2]",
      },
      arr2[1],
    );

    var arr3 = [null, "b", "c"];
    await assertTransformation(
      arr3,
      {
        $$if: "$[0]",
        then: "$[1]",
        else: "$[2]",
      },
      arr3[2],
    );

    var arr4 = ["false", "b", "c"];
    await assertTransformation(
      arr4,
      {
        $$if: "$$boolean:$[0]",
        then: "$[1]",
        else: "$[2]",
      },
      arr4[2],
    );
  });

  test("inline", async () => {
    await assertTransformation(true, "$$if(a,b):$", "a");
    await assertTransformation(false, "$$if(a,b):$", "b");
    await assertTransformation(false, "$$if(a):$", null);
  });
});
