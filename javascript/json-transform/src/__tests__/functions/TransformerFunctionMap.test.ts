import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionMap", () => {
  test("testObjectFunctionMap", async () => {
    const source = {
      value: 5,
      item: {
        foo: "aaa",
        id: "bbb",
      },
      items: [
        { foo: "bar", id: "aaa" },
        { foo: "bar2", id: "bbb" },
      ],
    };
    await assertTransformation(
      source,
      {
        $$map: [
          "$.items",
          {
            id: "##current.id",
            map_foo: "##current.foo",
            idx: "##index",
            value: "$.value",
          },
        ],
      },
      [
        { id: "aaa", map_foo: "bar", idx: 0, value: 5 },
        { id: "bbb", map_foo: "bar2", idx: 1, value: 5 },
      ],
    );

    await assertTransformation(
      source,
      {
        $$map: "$.items",
        to: {
          id: "##current.id",
          map_foo: "##current.foo",
          idx: "##index",
          value: "$.value",
        },
      },
      [
        { id: "aaa", map_foo: "bar", idx: 0, value: 5 },
        { id: "bbb", map_foo: "bar2", idx: 1, value: 5 },
      ],
    );

    var valueLookup = {
      $$map: [
        "$.item",
        {
          id: "##current.id",
          map_foo: "##current.foo",
          idx: "##index",
          value: "$.value",
        },
      ],
    };
    await assertTransformation(source, valueLookup, null);
  });

  test("objectNonTransformed", async () => {
    await assertTransformation(
      {
        a: [1, 2],
        b: [2, 4],
      },
      {
        $$map: [["$.a", "$.b"], "##current[1]"],
      },
      [2, 4],
    );
  });

  test("inline", async () => {
    await assertTransformation([{ a: 10 }, { a: 11 }, { a: 12 }], "$$map(##current.a):$", [10, 11, 12]);
  });
});
