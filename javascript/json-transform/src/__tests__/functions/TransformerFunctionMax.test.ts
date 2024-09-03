import type BigNumber from "bignumber.js";
import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";
import { BigDecimal } from "../../functions/common/FunctionHelpers";

describe("TransformerFunctionMax", () => {
  test("inline", async () => {
    const arr = [4, -2, 13.45, null];
    await assertTransformation(arr, "$$max($$long:40):$", BigInt(40));
    await assertTransformation(arr, "$$max(-8,NUMBER):$", 13.45);
    await assertTransformation(arr, "$$max():$", 13.45);
    await assertTransformation(arr, "$$max(z,STRING):$", "z");
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
        $$max: "$",
        by: "##current.value",
        default: "zz",
        type: "STRING",
      },
      "zz",
    );

    await assertTransformation(
      arr,
      {
        $$max: "$",
        by: "##current.value",
      },
      BigDecimal("13.45"),
    );
  });
});
