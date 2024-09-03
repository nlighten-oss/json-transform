import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";
import { BigDecimal } from "../../functions/common/FunctionHelpers";

describe("TransformerFunctionMath", () => {
  test("inline", async () => {
    const arr = [BigDecimal(4), BigDecimal(2)];
    await assertTransformation(arr, "$$math(+,$[0],$[1])", BigDecimal(6));
    await assertTransformation(arr, "$$math($[0],+,$[1])", BigDecimal(6));
    await assertTransformation(arr, "$$math($[0],+)", arr[0]);
    await assertTransformation(arr, "$$math($[0],-,$[1])", BigDecimal(2));
    await assertTransformation(arr, "$$math($[0],*,$[1])", BigDecimal(8));
    await assertTransformation(arr, "$$math($[0],/,$[1])", BigDecimal(2));
    await assertTransformation(arr, "$$math($[0],//,3)", BigDecimal(1));
    await assertTransformation(arr, "$$math($[0],%,3)", BigDecimal(1));
    await assertTransformation(arr, "$$math($[1],^,3)", BigDecimal(8));
    await assertTransformation(arr, "$$math(3,&,1)", BigDecimal(1));
    await assertTransformation(arr, "$$math(6,AND,3)", BigDecimal(2));
    await assertTransformation(arr, "$$math(6,|,3)", BigDecimal(7));
    await assertTransformation(arr, "$$math(6,~,3)", BigDecimal(5));
    await assertTransformation(arr, "$$math(6,>>,1)", BigDecimal(3));
    await assertTransformation(arr, "$$math(6,<<,3)", BigDecimal(48));
    await assertTransformation(arr, "$$math(MIN,$[0],$[1])", BigDecimal(2));
    await assertTransformation(arr, "$$math(MAX,$[0],$[1])", BigDecimal(4));
    await assertTransformation(arr, "$$math(SQRT,81)", BigDecimal(9));
    await assertTransformation(arr, "$$math(SQRT):81", BigDecimal(9));
    await assertTransformation(arr, "$$math(SQRT):$$math($[0],^,2)", arr[0]);
    await assertTransformation(arr, "$$math(ROUND,4.6)", BigDecimal(5));
    await assertTransformation(arr, "$$math(ROUND):4.6", BigDecimal(5));
    await assertTransformation(arr, "$$math(ROUND,1):4.66", BigDecimal(4.7));
    await assertTransformation(arr, "$$math(ROUND,4.66,1)", BigDecimal(4.7));
    await assertTransformation(arr, "$$math(4.66,ROUND,1)", BigDecimal(4.7));
    await assertTransformation(arr, "$$math(FLOOR,4.6)", BigDecimal(4));
    await assertTransformation(arr, "$$math(FLOOR):4.6", BigDecimal(4));
    await assertTransformation(arr, "$$math(FLOOR,1):4.66", BigDecimal(4.6));
    await assertTransformation(arr, "$$math(CEIL,4.2)", BigDecimal(5));
    await assertTransformation(arr, "$$math(CEIL):4.2", BigDecimal(5));
    await assertTransformation(arr, "$$math(CEIL,1):4.22", BigDecimal(4.3));
    await assertTransformation(arr, "$$math(ABS,-10)", BigDecimal(10));
    await assertTransformation(arr, "$$math(ABS):-10", BigDecimal(10));
    await assertTransformation(arr, "$$math(NEG,$[0])", BigDecimal(-4));
    await assertTransformation(arr, "$$math(NEG):$[0]", BigDecimal(-4));
    await assertTransformation(arr, "$$math(SIG):42", BigDecimal(1));
    await assertTransformation(arr, "$$math(SIGNUM):-42", BigDecimal(-1));
    await assertTransformation(arr, "$$math(SIG):0", BigDecimal(0));
  });

  test("object", async () => {
    const arr = [BigDecimal(4), new BigDecimal(2)];
    await assertTransformation(
      arr,
      {
        $$math: ["+", "$[0]", "$[1]"],
      },
      BigDecimal(6),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["$[0]", "+", "$[1]"],
      },
      BigDecimal(6),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["-", "$[0]", "$[1]"],
      },
      BigDecimal(2),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["*", "$[0]", "$[1]"],
      },
      BigDecimal(8),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["/", "$[0]", "$[1]"],
      },
      BigDecimal(2),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["//", "$[0]", "3"],
      },
      BigDecimal(1),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["$[0]", "//", 3],
      },
      BigDecimal(1),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["%", "$[0]", "3"],
      },
      BigDecimal(1),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["^", "$[1]", "3"],
      },
      BigDecimal(8),
    );

    await assertTransformation(arr, { $$math: [3, "&", 1] }, BigDecimal(1));
    await assertTransformation(arr, { $$math: [6, "&", 3] }, BigDecimal(2));
    await assertTransformation(arr, { $$math: [6, "|", 3] }, BigDecimal(7));
    await assertTransformation(arr, { $$math: [6, "XOR", 3] }, BigDecimal(5));
    await assertTransformation(arr, { $$math: [6, ">>", 1] }, BigDecimal(3));
    await assertTransformation(arr, { $$math: [6, "<<", 3] }, BigDecimal(48));
    await assertTransformation(
      arr,
      {
        $$math: ["sqrt", 81],
      },
      BigDecimal(9),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["min", "$[0]", "$[1]"],
      },
      BigDecimal(2),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["max", "$[0]", "$[1]"],
      },
      BigDecimal(4),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["round", 4.6],
      },
      BigDecimal(5),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["round", 4.66, 1],
      },
      BigDecimal(4.7),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["floor", 4.6],
      },
      BigDecimal(4),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["floor", 4.66, 1],
      },
      BigDecimal(4.6),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["ceil", 4.2],
      },
      BigDecimal(5),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["ceil", 4.22, 1],
      },
      BigDecimal(4.3),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["abs", -10],
      },
      BigDecimal(10),
    );
    await assertTransformation(
      arr,
      {
        $$math: ["neg", "$[0]"],
      },
      BigDecimal(-4),
    );
    await assertTransformation(
      ["abs", -10],
      {
        $$math: "$",
      },
      BigDecimal(10),
    );

    await assertTransformation(arr, { $$math: ["SIG", 42] }, BigDecimal(1));
    await assertTransformation(arr, { $$math: ["SIGNUM", -42] }, BigDecimal(-1));
    await assertTransformation(arr, { $$math: ["SIG", 0] }, BigDecimal(0));
  });

  test("combineScaling", async () => {
    await assertTransformation(null, "$$decimal(2):$$math(1,*,0.987654321)", BigDecimal(0.99));
    await assertTransformation(null, "$$decimal(2,FLOOR):$$math(1,*,0.987654321)", BigDecimal(0.98));
  });
});
