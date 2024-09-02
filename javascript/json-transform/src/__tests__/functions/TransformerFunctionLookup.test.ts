import { describe, test } from "vitest";
import { assertTransformation } from "../BaseTransformationTest";
import { BigDecimal } from "../../functions/common/FunctionHelpers";
import BigNumber from "bignumber.js";

describe("TransformerFunctionLookup", () => {
  test("mergeWithBy", async () => {
    await assertTransformation(
      {
        a: [
          { id: 2, a: 1 },
          { id: 5, a: 2 },
        ],
        b: [
          { id: 2, a: "x" },
          { id: 5, e: true },
        ],
      },

      {
        $$lookup: "$.a",
        using: [
          {
            with: "$.b",
            as: "match",
            on: {
              $$is: "##current.id",
              eq: "##match.id",
            },
          },
        ],
      },
      [
        { id: 2, a: "x" },
        { id: 5, a: 2, e: true },
      ],
    );

    await assertTransformation(
      {
        a: [
          { id: 2, a: 1 },
          { id: 5, a: 2 },
        ],
        b: [
          { key: 2, a: "x" },
          { key: 5, e: true },
        ],
      },

      {
        $$lookup: "$.a",
        using: [
          {
            with: "$.b",
            as: "match",
            on: {
              $$is: "##current.id",
              eq: "##match.key",
            },
          },
        ],
      },
      [
        { id: 2, a: "x", key: 2 },
        { id: 5, a: 2, e: true, key: 5 },
      ],
    );
  });

  test("mergeWithTo", async () => {
    // don't override a, just copy e
    await assertTransformation(
      {
        a: [
          { id: 2, a: 1 },
          { id: 5, a: 2 },
        ],
        b: [
          { id: 2, a: "x" },
          { id: 5, e: true },
        ],
      },

      {
        $$lookup: "$.a",
        using: [
          {
            with: "$.b",
            as: "match",
            on: {
              $$is: "##current.id",
              eq: "##match.id",
            },
          },
        ],
        to: {
          "*": "##current",
          e: "##match.e",
        },
      },
      [
        { id: 2, a: 1 },
        { id: 5, a: 2, e: true },
      ],
    );

    await assertTransformation(
      {
        a1: [
          { id: "aaa", val: "a" },
          { id: "bbb", val: "b" },
        ],
        a2: [
          { name: "aaa", val: "A" },
          { name: "bbb", val: "B" },
        ],
      },
      {
        $$lookup: "$.a1",
        using: [
          {
            with: "$.a2",
            as: "a2",
            on: {
              $$is: "##current.id",
              eq: "##a2.name",
            },
          },
        ],
        to: ["##current.val", "##a2.val"],
      },
      [
        ["a", "A"],
        ["b", "B"],
      ],
    );
  });
});
