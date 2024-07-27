import { describe, test } from "vitest";
import { assertTransformation} from "../BaseTransformationTest";
import {BigDecimal} from "../../functions/common/FunctionHelpers";

describe("TransformerFunctionAt", () => {
  test("inline", () => {
    const arr = [4, 2, 13];
    assertTransformation(arr, "$$at(0):$", 4);
    assertTransformation(arr, "$$at(1):$", 2);
    assertTransformation(arr, "$$at(-1):$", 13);
    assertTransformation(arr, "$$at(3):$", null);
    assertTransformation(arr, "$$at:$", null);
  });

  test("object", () => {
    const arr = [4, 2, BigDecimal(13)];
    assertTransformation(arr, {"$$at": "$", index: 0}, 4);
    assertTransformation(arr, {"$$at": "$", index: 1}, 2);
    assertTransformation(arr, {"$$at": "$", index: -1}, BigDecimal(13));
    assertTransformation(arr, {"$$at": "$", index: 3}, null);
    assertTransformation(arr, {"$$at": "$"}, null);
  });
});
