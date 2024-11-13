import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionJsonPointer", () => {
  test("get", async () => {
    await assertTransformation(
      {
        b: [
          "c",
          "d",
          {
            e: "Hello",
          },
        ],
      },
      "$$jsonpointer(GET,/b/2/e):$",
      "Hello",
    );
  });

  test("getYoDawg", async () => {
    await assertTransformation(
      {
        pointer: "/pointer",
      },
      "$$jsonpointer(get,$.pointer):$",
      "/pointer",
    );
  });

  test("set", async () => {
    await assertTransformation(
      {
        b: [
          "c",
          "d",
          {
            e: "Hello",
          },
        ],
      },
      "$$jsonpointer(SET,/b,'$.b[2]'):$",
      {
        b: {
          e: "Hello",
        },
      },
    );
  });
  test("remove", async () => {
    await assertTransformation(
      {
        b: [
          "c",
          "d",
          {
            e: "Hello",
          },
        ],
      },
      "$$jsonpointer(REMOVE,/b/2):$",
      {
        b: ["c", "d"],
      },
    );
  });
});
