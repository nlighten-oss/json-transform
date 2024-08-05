import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionCsvParse", () => {
  test("inline", async () => {
    await assertTransformation('a\n","', "$$csvparse:$", [{ a: "," }]);
    await assertTransformation('a\n""""', "$$csvparse:$", [{ a: '"' }]);
    await assertTransformation("1,2\n3,4", "$$csvparse(true):$", [
      ["1", "2"],
      ["3", "4"],
    ]);
  });

  test("object", async () => {
    await assertTransformation(
      'a\n","',
      {
        $$csvparse: "$",
      },
      [{ a: "," }],
    );
    await assertTransformation(
      'a\n""""',
      {
        $$csvparse: "$",
      },
      [{ a: '"' }],
    );
  });
});
