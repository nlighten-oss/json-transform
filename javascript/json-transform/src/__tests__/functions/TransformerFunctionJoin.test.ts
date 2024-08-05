import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";

describe("TransformerFunctionJoin", () => {
  test("object", async () => {
    const arr = ["a", "b", "c"];
    await assertTransformation(
      arr,
      {
        $$join: "$",
      },
      "abc",
    );
    await assertTransformation(
      arr,
      {
        $$join: ["$[0]", "B"],
      },
      "aB",
    );
    await assertTransformation(
      arr,
      {
        $$join: ["$[0]", null, null, "B"],
        delimiter: ",",
      },
      "a,B",
    );
    await assertTransformation(
      arr,
      {
        $$join: "$",
        $$delimiter: ",",
      },
      "a,b,c",
    );
  });

  test("object_prefix_suffix", async () => {
    const arr = ["hello", "world"];
    await assertTransformation(
      arr,
      {
        $$join: "$",
        delimiter: " ",
        prefix: "<",
      },
      "<" + arr.join(" "),
    );
    await assertTransformation(
      arr,
      {
        $$join: "$",
        delimiter: " ",
        prefix: "<",
        suffix: ">",
      },
      "<" + arr.join(" ") + ">",
    );
  });

  test("inline", async () => {
    const list = ["hello", " ", "world"];
    await assertTransformation(list, "$$join:$", list.join(""));
    await assertTransformation(list, "$$join():$", list.join(""));
    const list2 = ["hello", 5, true];
    await assertTransformation(list2, "$$join:$", list2.map(v => `${v}`).join(""));
    const arr = ["hello", " ", "world"];
    await assertTransformation(arr, "$$join:$", arr.join(""));
    const ja = arr.slice();
    await assertTransformation(ja, "$$join:$", arr.join(""));

    const withNulls = ["hello", null, "world"];
    await assertTransformation(withNulls, "$$join(' '):$", "hello world");
    await assertTransformation(withNulls, "$$join(' ',,,true):$", "hello null world");
  });
  test("inline_delimiter", async () => {
    const arr = ["hello", " ", "world"];
    await assertTransformation(arr, "$$join(,):$", arr.join(""));
    await assertTransformation(arr, "$$join(:):$", arr.join(":"));
    await assertTransformation(arr, "$$join(','):$", arr.join(","));
    await assertTransformation(arr, "$$join('\\''):$", arr.join("'"));
    await assertTransformation(arr, "$$join('$[1]'):$", arr.join(arr[1]));
  });

  test("inline_prefix_suffix", async () => {
    const arr = ["hello", "world"];
    await assertTransformation(arr, "$$join( ,<):$", "<" + arr.join(" "));
    await assertTransformation(arr, "$$join( ,<,>):$", "<" + arr.join(" ") + ">");
  });
});
