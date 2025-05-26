import { describe, expect, test } from "vitest";
import { convertFunctionsToObjects, tryConvertFunctionsToInline } from "../../utils/convert";

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
    ).toEqual("$$map('$$substring(0,5):##current.name'):$$flat:$.names");
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
        $$math: true,
        op: "+",
        op1: "##accumulator",
        op2: "##current.amount",
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
});
