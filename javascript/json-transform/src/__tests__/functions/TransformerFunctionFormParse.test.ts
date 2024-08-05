import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionCsv", () => {
  test("inline", async () => {
    await assertTransformation("a=1&b=B&c", "$$formparse:$", {
      a: "1",
      a$$: ["1"],
      b: "B",
      b$$: ["B"],
      c: "true",
      c$$: ["true"],
    });

    // arrays
    await assertTransformation("a=1&a=2", "$$formparse:$", {
      a: "1",
      a$$: ["1", "2"],
    });
  });

  test("object", async () => {
    await assertTransformation(
      "a=1&b=B&c",
      { $$formparse: "$" },
      {
        a: "1",
        a$$: ["1"],
        b: "B",
        b$$: ["B"],
        c: "true",
        c$$: ["true"],
      },
    );

    // arrays
    await assertTransformation(
      "a=1&a=2",
      { $$formparse: "$" },
      {
        a: "1",
        a$$: ["1", "2"],
      },
    );
  });
});
