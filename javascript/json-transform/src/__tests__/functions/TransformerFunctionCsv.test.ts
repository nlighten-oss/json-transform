import { describe, test } from "vitest";
import { assertTransformation} from "../BaseTransformationTest";

describe("TransformerFunctionCsv", () => {
  test("inline", () => {
    assertTransformation(      [{"a":"A","b":1},{"a":"C","b":2}],
    "$$csv:$",
"a,b\nA,1\nC,2\n");
    assertTransformation([{"a":"A","b":1},{"a":"C","b":2}],
    "$$csv(true):$",
      "A,1\nC,2\n");
  });

  test("object", () => {
    assertTransformation(
      [{"a":"A","b":1},{"a":"C","b":2}],
    {
      "$$csv": "$"
    }, "a,b\nA,1\nC,2\n");
    assertTransformation(
        [{"a":"A","b":1},{"a":"C","b":2}],
    {
      "$$csv": "$",
      "no_headers": true
    }, "A,1\nC,2\n");
  });

  test("object_names", () => {
    assertTransformation([[1,2],[3,4]], {
      "$$csv": "$",
      "names": ["a","b"]
    }, "a,b\n1,2\n3,4\n");
    // without names
    assertTransformation([[1,2],[3,4]], { "$$csv": "$" },  "1,2\n3,4\n");
  });
});
