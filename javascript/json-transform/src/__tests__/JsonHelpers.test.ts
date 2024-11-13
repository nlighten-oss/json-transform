import { describe, expect, test } from "vitest";
import { mergeInto } from "../JsonHelpers";

describe("JsonHelpers", () => {
  test("mergeInto - GivenMutuallyExclusiveKeysWithDot", () => {
    var root = {
      "numbers.roman": { I: 1, II: 2 },
    };
    var mergee = {
      "numbers.exist": true,
    };
    var expected = {
      "numbers.roman": { I: 1, II: 2 },
      "numbers.exist": true,
    };
    expect(expected).toEqual(mergeInto(root, mergee, null));
  });

  test("mergeInto - GivenNoPath", () => {
    var root = {
      "a.b.c[0]": "foovalue",
    };
    var mergee = {
      a: { z: "barvalue" },
    };
    var expected = {
      a: { z: "barvalue" },
      "a.b.c[0]": "foovalue",
    };
    expect(expected).toEqual(mergeInto(root, mergee, null));
  });

  test("mergeInto - GivenMutuallyExclusiveKeysAndDollarPath", () => {
    var root = {
      roman: { I: 1, II: 2 },
    };
    var mergee = {
      arithmetics: { exist: true },
      symbols: ["I", "V", "X", "L", "C", "D", "M"],
    };
    var expected = {
      roman: { I: 1, II: 2 },
      arithmetics: { exist: true },
      symbols: ["I", "V", "X", "L", "C", "D", "M"],
    };
    expect(expected).toEqual(mergeInto(root, mergee, "$"));
  });
});
