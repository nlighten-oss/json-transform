import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionUpper", () => {
  test("uppercase", async () => {
    const strVal = "text";
    await assertTransformation(strVal, "$$upper:$", strVal.toUpperCase());
    await assertTransformation(strVal, "$$upper():$", strVal.toUpperCase());
  });
});
