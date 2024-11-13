import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionPad", () => {
  test("inline", async () => {
    const strVal = "text";
    await assertTransformation(strVal, "$$pad(right,3,2):$", strVal);
    await assertTransformation(strVal, "$$pad(end,6):" + strVal, strVal + "00");
    await assertTransformation(strVal, "$$pad(end,6):$", strVal + "00");
    await assertTransformation(strVal, "$$pad(right,6,0):$", strVal + "00");
    await assertTransformation(strVal, "$$pad(end,6,' '):$", strVal + "  ");
    await assertTransformation(strVal, "$$pad(left,6,' '):$", "  " + strVal);
    await assertTransformation(strVal, "$$pad(left,6,x):$", "xx" + strVal);
    await assertTransformation(strVal, "$$pad(start,6,xy):$", "xy" + strVal);
    await assertTransformation(strVal, "$$pad(start,9,xy):$", "xyxyx" + strVal);

    // bad inputs
    await assertTransformation(strVal, "$$pad:$", strVal);
    await assertTransformation(strVal, "$$pad:$", strVal);
    await assertTransformation(strVal, "$$pad():$", strVal);
    await assertTransformation(strVal, "$$pad(start):$", strVal);
  });
});
