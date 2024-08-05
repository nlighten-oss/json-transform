import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";
import BigNumber from "bignumber.js";
import { BigDecimal } from "../../functions/common/FunctionHelpers";

describe("TransformerFunctionEval", () => {
  class Holder {
    public value: BigNumber | null;
    constructor(value: BigNumber | null) {
      this.value = value;
    }
  }

  test("inline", async () => {
    await assertTransformation(
      {
        a: 42,
        b: "$.a",
      },
      "$$eval:$.b",
      42,
    );
  });

  test("object", async () => {
    const arr = [
      new Holder(new BigDecimal(4)),
      new Holder(new BigDecimal(2)),
      new Holder(new BigDecimal("13.45")),
      new Holder(null),
    ];
    await assertTransformation(
      arr,
      {
        $$eval: {
          $$join: ["\\$", "$avg:", "\\$", "..value"],
        },
      },
      BigDecimal(4.8625),
    );

    await assertTransformation(
      arr,
      {
        $$eval: {
          $$jsonparse: {
            $$join: ["{", '"$$avg"', ":", '"$"', ', "by":"##current.value"', "}"],
          },
        },
      },
      BigDecimal(4.8625),
    );
  });
});
