import { describe, expect, test } from "vitest";
import { convertFunctionsToObjects, tryConvertFunctionsToInline } from "../../utils/convert";
import definitions from "../../functions/definitions";

describe("convert", () => {
  test("object to inline - test 1", () => {
    expect(
      tryConvertFunctionsToInline({
        $$map: {
          $$flat: "$.names",
        },
        to: "##current.name",
      }),
    ).toEqual("$$map('##current.name'):$$flat:$.names");
  });
  test("object to inline - test 2", () => {
    expect(
      tryConvertFunctionsToInline({
        $$map: {
          $$flat: "$.names",
        },
        to: {
          name: "##current.name",
        },
      }),
    ).toEqual({
      $$map: "$$flat:$.names",
      to: {
        name: "##current.name",
      },
    });
  });
  test("object to inline - test 3", () => {
    expect(
      tryConvertFunctionsToInline({
        $$map: "$.names",
        to: {
          name: "##current.name",
        },
      }),
    ).toEqual({
      $$map: "$.names",
      to: {
        name: "##current.name",
      },
    });
  });
  test("object to inline - test 4", () => {
    expect(
      tryConvertFunctionsToInline({
        $$map: {
          $$flat: "$.names",
        },
        to: "$$substring(0,5):##current.name",
      }),
    ).toEqual({
      $$map: "$$flat:$.names",
      to: "$$substring(0,5):##current.name",
    });
  });
  test("object to inline - test 5", () => {
    expect(
      tryConvertFunctionsToInline({
        $$pad: "$.total",
        direction: "LEFT",
        width: 8,
      }),
    ).toEqual("$$pad(LEFT,8):$.total");
  });
  test("object to inline - test 6", () => {
    expect(
      tryConvertFunctionsToInline({
        $$range: [1, 10, 2],
      }),
    ).toEqual("$$range(1,10,2)");
  });

  test("object to inline - test 7", () => {
    expect(
      tryConvertFunctionsToInline({
        $$math: [1, "/", 2],
      }),
    ).toEqual("$$math(1,/,2)");
  });

  test.skip("object to inline - test 8", () => {
    expect(
      tryConvertFunctionsToInline({
        $$math: 1,
        op: "/",
        op2: 2,
      }),
    ).toEqual("$$math(1,/,2)");
  });
  test("object to inline - test 9", () => {
    expect(
      tryConvertFunctionsToInline({
        "*": "$",
        full_name: {
          $$join: ["$.first_name", "$.last_name"],
          delimiter: " ",
        },
        age: {
          $$math: [
            {
              $$math: [
                {
                  $$date: "#now",
                  format: "EPOCH",
                },
                "-",
                {
                  $$date: "$.date_of_birth",
                  format: "EPOCH",
                },
              ],
            },
            "//",
            {
              $$math: [
                365,
                "*",
                {
                  $$math: [24, "*", 3600],
                },
              ],
            },
          ],
        },
      }),
    ).toEqual({
      "*": "$",
      full_name: {
        $$join: ["$.first_name", "$.last_name"],
        delimiter: " ",
      },
      age: {
        $$math: [
          {
            $$math: ["$$date(EPOCH):#now", "-", "$$date(EPOCH):$.date_of_birth"],
          },
          "//",
          "$$math(365,*,'$$math(24,*,3600)')",
        ],
      },
    });
  });

  // -------------------------------------------

  test("inline to object - test 1", () => {
    expect(convertFunctionsToObjects("$$map('##current.name'):$$flat:$.names")).toEqual({
      $$map: {
        $$flat: "$.names",
      },
      to: "##current.name",
    });
  });
  test("inline to object - test 2", () => {
    expect(convertFunctionsToObjects("$$reduce('$$math(##accumulator,+,##current.amount)',0):$.items")).toEqual({
      $$reduce: "$.items",
      identity: "0",
      to: {
        $$math: ["##accumulator", "+", "##current.amount"],
      },
    });
  });
  test("inline to object - test 3", () => {
    expect(convertFunctionsToObjects("$$pad(LEFT,8):$.total")).toEqual({
      $$pad: "$.total",
      direction: "LEFT",
      width: 8,
    });
  });
  test("inline to object - test 4", () => {
    expect(convertFunctionsToObjects("$$range(1,10,2)")).toEqual({
      $$range: [1, 10, 2],
    });
  });
  test("inline to object - test 5", () => {
    expect(convertFunctionsToObjects("$$math(1,/,2)")).toEqual({
      $$math: [1, "/", 2],
    });
  });

  test.skip("test 999", () => {
    // is, lookup, sort
    // math, range
    const x: string[] = [];
    (Object.keys(definitions) as (keyof typeof definitions)[]).forEach(def => {
      if (!definitions[def].arguments) {
        return;
      }
      let hasInline = false;
      let hasObjectOnly = false;
      definitions[def].arguments?.forEach(arg => {
        if (typeof arg.position === "number") {
          hasInline = true;
        } else {
          hasObjectOnly = true;
        }
      });
      if (hasInline && hasObjectOnly) {
        x.push(def);
      }
    });
    expect(x).toEqual([]);
  });
});
