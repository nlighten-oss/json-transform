import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionGroup", () => {
  test("inline", async () => {
    await assertTransformation(
      [
        { o: 1, p: 11, w: "aaa" },
        { o: 1, p: 13, w: "bbb" },
        { o: 1, p: 11, w: "ccc" },
        { o: 2, p: 11, w: "ddd" },
        { o: 2, p: 13, w: "eee" },
        { o: 3, p: 12, w: "fff" },
        { o: 1, p: 9, w: "zzz" },
        { no_o: false, p: 9, w: "zzz" },
      ],
      "$$group(##current.o):$",
      {
        "": [{ no_o: false, p: 9, w: "zzz" }],
        "1": [
          { o: 1, p: 11, w: "aaa" },
          { o: 1, p: 13, w: "bbb" },
          { o: 1, p: 11, w: "ccc" },
          { o: 1, p: 9, w: "zzz" },
        ],
        "2": [
          { o: 2, p: 11, w: "ddd" },
          { o: 2, p: 13, w: "eee" },
        ],
        "3": [{ o: 3, p: 12, w: "fff" }],
      },
    );
    await assertTransformation(
      [
        ["a", 0, 1],
        ["a", 1, true],
        ["a", 2, "C"],
        ["b", 1, 6],
      ],
      "$$group(##current[0]):$",
      {
        a: [
          ["a", 0, 1],
          ["a", 1, true],
          ["a", 2, "C"],
        ],
        b: [["b", 1, 6]],
      },
    );

    // invalid input will yield an empty object
    await assertTransformation(null, "$$group", {});
    await assertTransformation(0.5, "$$group(##current[0]):$", {});
    await assertTransformation("test", "$$group(##current[0]):$", {});
    await assertTransformation(false, "$$group:$", {});
  });

  test("object", async () => {
    await assertTransformation(
      [
        { o: 1, p: 11, w: "aaa" },
        { o: 1, p: 13, w: "bbb" },
        { o: 1, p: 11, w: "ccc" },
        { o: 2, p: 11, w: "ddd" },
        { o: 2, p: 13, w: "eee" },
        { o: 3, p: 12, w: "fff" },
        { o: 1, p: 9, w: "zzz" },
        { no_o: false, p: 9, w: "zzz" },
      ],

      {
        $$group: "$",
        by: "##current.o",
        then: [
          {
            by: {
              $$join: ["p_", "##current.p"],
            },
            order: "DESC",
          },
        ],
      },

      {
        "": {
          p_9: [{ no_o: false, p: 9, w: "zzz" }],
        },
        "1": {
          p_9: [{ o: 1, p: 9, w: "zzz" }],
          p_13: [{ o: 1, p: 13, w: "bbb" }],
          p_11: [
            { o: 1, p: 11, w: "aaa" },
            { o: 1, p: 11, w: "ccc" },
          ],
        },
        "2": {
          p_13: [{ o: 2, p: 13, w: "eee" }],
          p_11: [{ o: 2, p: 11, w: "ddd" }],
        },
        "3": {
          p_12: [{ o: 3, p: 12, w: "fff" }],
        },
      },
    );

    await assertTransformation(null, { $$group: "$" }, {});
    await assertTransformation(0.5, { $$group: "$" }, {});
    await assertTransformation(false, { $$group: "$" }, {});
  });

  test("objectLazyBy", async () => {
    await assertTransformation(
      [
        { o: 1, p: 11, w: "aaa" },
        { o: 1, p: 13, w: "bbb" },
        { o: 1, p: 11, w: "ccc" },
        { o: 2, p: 11, w: "ddd" },
        { o: 2, p: 13, w: "eee" },
        { o: 3, p: 12, w: "fff" },
        { o: 1, p: 9, w: "zzz" },
        { no_o: false, p: 9, w: "zzz" },
      ],

      {
        $$group: "$",
        by: { $$join: ["##current.o", "##current.p"] },
      },

      {
        "111": [
          { o: 1, p: 11, w: "aaa" },
          { o: 1, p: 11, w: "ccc" },
        ],
        "113": [{ o: 1, p: 13, w: "bbb" }],
        "19": [{ o: 1, p: 9, w: "zzz" }],
        "211": [{ o: 2, p: 11, w: "ddd" }],
        "213": [{ o: 2, p: 13, w: "eee" }],
        "312": [{ o: 3, p: 12, w: "fff" }],
        "9": [{ no_o: false, p: 9, w: "zzz" }],
      },
    );
  });
});
