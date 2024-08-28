import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";
import { BigDecimal } from "../../functions/common/FunctionHelpers";

describe("TransformerFunctionJsonPath", () => {
  test("yoDawg", async () => {
    await assertTransformation(
      {
        path: "$.path",
      },
      "$$jsonpath($.path):$",
      "$.path",
    );
  });

  test("inArray", async () => {
    await assertTransformation(
      {
        arr: [null, "boo"],
      },
      "$$jsonpath('\\\\$.arr[1]'):$",
      "boo",
    );
  });

  test("multipleResults", async () => {
    await assertTransformation(
      [
        { id: 1, active: true },
        { id: 3, active: false },
        { id: 4, active: true },
        { id: 5, active: false },
      ],
      "$$jsonpath('\\\\$[?(@.active == true)]'):$",
      [
        { id: 1, active: true },
        { id: 4, active: true },
      ],
    );
  });
});
