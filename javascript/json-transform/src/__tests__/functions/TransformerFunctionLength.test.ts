import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionLength", () => {
  test("autoDetect", async () => {
    const str = "Hello World";
    await assertTransformation(str, "$$length:hello world", 11);
    await assertTransformation(str, "$$length():$", 11);
    const arr = ["Hello", "World"];
    await assertTransformation(arr, "$$length:$", 2);
    const obj = { a: "Hello", b: "World", c: "foo", d: "bar" };
    await assertTransformation(obj, "$$length:$", 4);
  });

  test("stringOnly", async () => {
    const str = "Hello World";
    await assertTransformation(str, "$$length(STRING):hello world", 11);
    await assertTransformation(str, "$$length(STRING):$", 11);
    const arr = ["Hello", "World"];
    await assertTransformation(arr, "$$length(STRING):$", null);
    const obj = { a: "Hello", b: "World", c: "foo", d: "bar" };
    await assertTransformation(obj, "$$length(STRING):$", null);
  });

  test("arrayOnly", async () => {
    const str = "Hello World";
    await assertTransformation(str, "$$length(ARRAY):hello world", null);
    await assertTransformation(str, "$$length(ARRAY):$", null);
    const arr = ["Hello", "World"];
    await assertTransformation(arr, "$$length(ARRAY):$", 2);
    const obj = { a: "Hello", b: "World", c: "foo", d: "bar" };
    await assertTransformation(obj, "$$length(ARRAY):$", null);
  });

  test("objectOnly", async () => {
    const str = "Hello World";
    await assertTransformation(str, "$$length(OBJECT):hello world", null);
    await assertTransformation(str, "$$length(OBJECT):$", null);
    const arr = ["Hello", "World"];
    await assertTransformation(arr, "$$length(OBJECT):$", null);
    const obj = { a: "Hello", b: "World", c: "foo", d: "bar" };
    await assertTransformation(obj, "$$length(OBJECT):$", 4);
  });

  test("zeroDefault", async () => {
    await assertTransformation(null, "$$length:$", null);
    await assertTransformation(null, "$$length(AUTO,true):$", 0);
    await assertTransformation(42, "$$length(AUTO,true):$", 0);
    await assertTransformation(true, "$$length(AUTO,true):$", 0);
  });
});
