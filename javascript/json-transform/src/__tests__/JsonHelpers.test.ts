import { describe, expect, test } from "vitest";
import { mergeInto, merge } from "../JsonHelpers";

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

  test("merge - all new", () => {
    expect(
      merge(
        {
          a: "A",
        },
        {
          b: "B",
        },
      ),
    ).toEqual({
      a: "A",
      b: "B",
    });
  });

  test("merge - override existing", () => {
    expect(
      merge(
        {
          a: "A",
          b: "B",
        },
        {
          b: "BB",
        },
      ),
    ).toEqual({
      a: "A",
      b: "BB",
    });
  });

  test("merge - override with null", () => {
    expect(
      merge(
        {
          a: "A",
          b: "B",
        },
        {
          b: null,
        },
      ),
    ).toEqual({
      a: "A",
      b: null,
    });
  });

  test("merge - shallow", () => {
    expect(
      merge(
        {
          a: {
            aa: "AA",
          },
          b: "B",
        },
        {
          a: {
            aaa: "AAA",
          },
        },
      ),
    ).toEqual({
      a: {
        aaa: "AAA",
      },
      b: "B",
    });
  });

  test("merge - deep", () => {
    expect(
      merge(
        {
          a: {
            aa: "AA",
          },
          b: "B",
        },
        {
          a: {
            aaa: "AAA",
          },
        },
        { deep: true },
      ),
    ).toEqual({
      a: {
        aa: "AA",
        aaa: "AAA",
      },
      b: "B",
    });
  });

  test("merge - deep and concatArray", () => {
    expect(
      merge(
        {
          a: {
            aa: "AA",
          },
          c: [1, 2],
        },
        {
          a: {
            aaa: "AAA",
          },
          c: [3, 4],
        },
        { deep: true, concatArrays: true },
      ),
    ).toEqual({
      a: {
        aa: "AA",
        aaa: "AAA",
      },
      c: [1, 2, 3, 4],
    });
  });

  test("merge - concatArray", () => {
    expect(
      merge(
        {
          a: {
            aa: "AA",
          },
          c: [1, 2],
        },
        {
          a: {
            aaa: "AAA",
          },
          c: [3, 4],
        },
        { concatArrays: true },
      ),
    ).toEqual({
      a: {
        aaa: "AAA",
      },
      c: [1, 2, 3, 4],
    });
  });
});
