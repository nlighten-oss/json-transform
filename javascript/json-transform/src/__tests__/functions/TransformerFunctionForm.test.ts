import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionCsv", () => {
  test("inline", async () => {
    await assertTransformation(
      {
        a: 1,
        b: "B",
        c: true,
      },
      "$$form:$",
      "a=1&b=B&c=true",
    );

    // arrays
    await assertTransformation(
      {
        a: [1, 2],
        c: true,
      },
      "$$form:$",
      "a=1&a=2&c=true",
    );
  });

  test("object", async () => {
    await assertTransformation(
      {
        a: 1,
        b: "B",
        c: true,
      },
      { $$form: "$" },
      "a=1&b=B&c=true",
    );

    // arrays
    await assertTransformation(
      {
        a: [1, 2],
        c: true,
      },
      { $$form: "$" },
      "a=1&a=2&c=true",
    );
  });
});
