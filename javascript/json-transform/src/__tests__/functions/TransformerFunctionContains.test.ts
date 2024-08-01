import { describe, test } from "vitest";
import { assertTransformation} from "../BaseTransformationTest";

describe("TransformerFunctionContains", () => {
  test("object", () => {
    assertTransformation([0, [], "a"], {
      "$$contains": "$", "that": "a"
    }, true);
    // with transformation
    assertTransformation("a", {
      "$$contains": ["b","$"], "that": "a"
    }, true);

    assertTransformation([0, [], "a"], {
      "$$contains": "$", "that": "b"
    }, false);
  });

  test("inline", () => {
    assertTransformation([0, [], "a"], "$$contains(a):$", true);
    assertTransformation([0, [], "a"], "$$contains(b):$", false);
  });
});
