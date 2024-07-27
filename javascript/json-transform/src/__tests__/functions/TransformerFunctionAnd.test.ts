import { describe, test } from "vitest";
import { assertTransformation} from "../BaseTransformationTest";

describe("TransformerFunctionAnd", () => {
  test("assertAnd", () => {
    const twoAndThree =
      {
        "$$and": [
          {"$$is": "$[0]", "eq": 2},
          {"$$is": "$[1]", "eq": 3}
        ]
      };
    assertTransformation([2,3], twoAndThree, true);
    assertTransformation([2,4], twoAndThree, false);
  });

  test("inline", () => {
      assertTransformation([null,0], "$$and:$", false);
      assertTransformation([1,0], "$$and:$", false);
      assertTransformation([1,true], "$$and:$", true);
  });
});
