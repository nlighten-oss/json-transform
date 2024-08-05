import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionFlat", () => {
  test("object", async () => {
    const arr = { value: "bbb" };
    await assertTransformation(
      arr,
      {
        $$flatten: { a: { a1: 123, a2: [1, 2, 3, { c: true }] }, b: "$.value" },
        array_prefix: "\\$",
      },
      { "a.a1": 123, "a.a2.$0": 1, "a.a2.$1": 2, "a.a2.$2": 3, "a.a2.$3.c": true, b: "bbb" },
    );

    await assertTransformation(
      arr,
      {
        $$flatten: { a: { a1: 123, a2: [1, 2, 3, { c: true }] }, b: "$.value" },
        prefix: "xxx",
        array_prefix: "",
      },
      { "xxx.a.a1": 123, "xxx.a.a2.0": 1, "xxx.a.a2.1": 2, "xxx.a.a2.2": 3, "xxx.a.a2.3.c": true, "xxx.b": "bbb" },
    );

    await assertTransformation(
      arr,
      {
        $$flatten: { a: { a1: 123, a2: [1, 2, 3, { c: true }] }, b: "$.value" },
        prefix: "xxx",
        array_prefix: "#null",
      },
      { "xxx.a.a1": 123, "xxx.a.a2": [1, 2, 3, { c: true }], "xxx.b": "bbb" },
    );
  });
});
