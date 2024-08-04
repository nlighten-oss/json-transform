import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";
import { BigDecimal } from "../../functions/common/FunctionHelpers";

class Holder {
  readonly value: number | null | bigint;

  constructor(value: number | null | bigint) {
    this.value = value;
  }
}

describe("TransformerFunctionAvg", () => {
  test("inline", async () => {
    const arr = [4, BigInt(2), 13.45, null];
    await assertTransformation(arr, "$$avg():$", BigDecimal(4.8625));
    await assertTransformation(arr, "$$avg(1):$", BigDecimal(5.1125));
  });

  test("object", async () => {
    const arr = [new Holder(BigInt(4)), new Holder(BigInt(2)), new Holder(13.45), new Holder(null)];
    await assertTransformation(arr, { $$avg: "$", by: "##current.value" }, BigDecimal(4.8625));
    await assertTransformation(arr, { $$avg: "$", by: "##current.value", default: 1 }, BigDecimal(5.1125));
  });
});
