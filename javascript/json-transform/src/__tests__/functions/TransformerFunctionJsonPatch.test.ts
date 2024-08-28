import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";
import { BigDecimal } from "../../functions/common/FunctionHelpers";

describe("TransformerFunctionJsonPatch", () => {
  test("add", async () => {
    await assertTransformation(
      {
        a: {
          b: "c",
        },
      },
      {
        $$jsonpatch: "$",
        ops: [{ op: "add", path: "/a/d", value: "e" }],
      },
      {
        a: {
          b: "c",
          d: "e",
        },
      },
    );
  });
});
