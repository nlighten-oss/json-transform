import { describe, test } from "vitest";
import { assertTransformation} from "../BaseTransformationTest";

describe("TransformerFunctionBoolean", () => {
  test("truthy", () => {
      assertTransformation(true, "$$boolean:$", true);
      // string
      assertTransformation("0", "$$boolean(js):$", true);
      assertTransformation("false", "$$boolean(js):$", true);
      assertTransformation("true", "$$boolean:$", true);
      assertTransformation("True", "$$boolean:$", true);
      assertTransformation("true", "$$boolean(JS):$", true);
      // number
      assertTransformation(1, "$$boolean:$", true);
      assertTransformation(-1, "$$boolean:$", true);
      assertTransformation(BigInt("1"), "$$boolean:$", true);
      // object
      assertTransformation({"":0}, "$$boolean:$", true);
      // arrays
      assertTransformation([0], "$$boolean:$", true);
    });

  test("falsy", () => {
      assertTransformation(false, "$$boolean:$", false);
      // string
      assertTransformation("", "$$boolean:$", false);
      assertTransformation("", "$$boolean(js):$", false);
      assertTransformation("0", "$$boolean:$", false);
      assertTransformation("false", "$$boolean:$", false);
      assertTransformation("False", "$$boolean:$", false);
      // number
      assertTransformation(0, "$$boolean:$", false);
      assertTransformation(BigInt(0), "$$boolean:$", false);
      // object
      assertTransformation(null, "$$boolean:$", false);
      assertTransformation({}, "$$boolean:$", false);
      // arrays
      assertTransformation([], "$$boolean:$", false);
  });

  test("object", () => {
      assertTransformation("true", JSON.parse("{\"$$boolean\":\"$\",\"style\":\"JS\"}"), true);
      assertTransformation("false", JSON.parse("{\"$$boolean\":\"$\",\"style\":\"js\"}"), true);
      assertTransformation("false", JSON.parse("{\"$$boolean\":\"$\"}"), false);
  });
});
