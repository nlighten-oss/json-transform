import { describe, test } from "vitest";
import { assertTransformation} from "../BaseTransformationTest";

describe("TransformerFunctionCsvParse", () => {
  test("inline", () => {
    assertTransformation("a\n\",\"", "$$csvparse:$", [{"a": ","}]);
    assertTransformation("a\n\"\"\"\"", "$$csvparse:$", [{"a": "\""}]);
    assertTransformation("1,2\n3,4", "$$csvparse(true):$", [["1", "2"], ["3", "4"]]);
  });

  test("object", () => {
    assertTransformation("a\n\",\"", {
        "$$csvparse": "$"
      },
      [{"a": ","}]);
    assertTransformation("a\n\"\"\"\"", {
        "$$csvparse": "$"
      },
      [{"a": "\""}]);
  });
});
