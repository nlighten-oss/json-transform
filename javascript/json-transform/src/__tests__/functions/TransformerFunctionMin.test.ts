import type BigNumber from "bignumber.js";
import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";
import { BigDecimal } from "../../functions/common/FunctionHelpers";

describe("TransformerFunctionMin", () => {
  test("inline", async () => {
    const arr = [4, -2, 13.45, null];
    await assertTransformation(arr, "$$min($$long:-4):$", BigInt(-4));
    await assertTransformation(arr, "$$min(-8,NUMBER):$", "-8");
    await assertTransformation(arr, "$$min():$", null);
    await assertTransformation(arr, "$$min(z,STRING):$", -2);
  });

  class Holder {
    public value: BigNumber | null;
    constructor(value: BigNumber | null) {
      this.value = value;
    }
  }

  test("object", async () => {
    const arr = [
      new Holder(BigDecimal(4)),
      new Holder(BigDecimal(2)),
      new Holder(BigDecimal("13.45")),
      new Holder(null),
    ];
    await assertTransformation(
      arr,
      {
        $$min: "$",
        by: "##current.value",
      },
      null,
    );

    await assertTransformation(
      arr,
      {
        $$min: "$",
        by: "##current.value",
        default: 1,
      },
      1,
    );
  });
});
