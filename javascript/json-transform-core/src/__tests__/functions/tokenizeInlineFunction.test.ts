import { describe, expect, test } from "vitest";
import { tokenizeInlineFunction } from "../../functions/tokenizeInlineFunction";

describe("tokenizeInlineFunction", () => {
  test("tokenizeInlineFunction - test 1", () => {
    expect(tokenizeInlineFunction("$$foo")).toEqual({
      name: "foo",
      keyLength: 5,
    });
  });
  test("tokenizeInlineFunction - test 2", () => {
    expect(tokenizeInlineFunction("$$foo(arg1,'arg2')")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 4,
          value: "arg1",
        },
        {
          index: 11,
          length: 6,
          value: "arg2",
        },
      ],
    });
  });
  test("tokenizeInlineFunction - test 3", () => {
    expect(tokenizeInlineFunction("$$foo:some input ")).toEqual({
      name: "foo",
      input: {
        index: 6,
        length: 11,
        value: "some input ",
      },
      keyLength: 5,
    });
  });
  test("tokenizeInlineFunction - test 4", () => {
    expect(tokenizeInlineFunction("$$foo(arg1,'arg2'):some input ")).toEqual({
      name: "foo",
      args: [
        {
          index: 6,
          length: 4,
          value: "arg1",
        },
        {
          index: 11,
          length: 6,
          value: "arg2",
        },
      ],
      input: {
        index: 19,
        length: 11,
        value: "some input ",
      },
      keyLength: 5,
    });
  });
  test("tokenizeInlineFunction - test 5", () => {
    expect(tokenizeInlineFunction("$$foo(arg1,'a(rg2'):some input ")).toEqual({
      name: "foo",
      args: [
        {
          index: 6,
          length: 4,
          value: "arg1",
        },
        {
          index: 11,
          length: 7,
          value: "a(rg2",
        },
      ],
      input: {
        index: 20,
        length: 11,
        value: "some input ",
      },
      keyLength: 5,
    });
  });
  test("tokenizeInlineFunction - test 6", () => {
    expect(tokenizeInlineFunction("$$foo(a(rg1,'a(rg2'):some input ")).toEqual({
      name: "foo",
      args: [
        {
          index: 6,
          length: 5,
          value: "a(rg1",
        },
        {
          index: 12,
          length: 7,
          value: "a(rg2",
        },
      ],
      input: {
        index: 21,
        length: 11,
        value: "some input ",
      },
      keyLength: 5,
    });
  });
  test("tokenizeInlineFunction - test 7", () => {
    expect(tokenizeInlineFunction("$$foo(a()rg1,'a(rg2'):some input ")).toBeUndefined();
  });
  test("tokenizeInlineFunction - test 8", () => {
    expect(tokenizeInlineFunction("$$foo('a()rg1','a(rg2'):some input ")).toEqual({
      name: "foo",
      args: [
        {
          index: 6,
          length: 8,
          value: "a()rg1",
        },
        {
          index: 15,
          length: 7,
          value: "a(rg2",
        },
      ],
      input: {
        index: 24,
        length: 11,
        value: "some input ",
      },
      keyLength: 5,
    });
  });

  test("tokenizeInlineFunction - test 9", () => {
    expect(tokenizeInlineFunction("$$map('##current.name'):$$flat:$.names")).toEqual({
      name: "map",
      args: [
        {
          index: 6,
          length: 16,
          value: "##current.name",
        },
      ],
      input: {
        index: 24,
        length: 14,
        value: "$$flat:$.names",
      },
      keyLength: 5,
    });
  });
  test("tokenizeInlineFunction - test 10", () => {
    expect(tokenizeInlineFunction("$$foo()")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 0,
          value: null,
        },
      ],
    });
  });
  test("tokenizeInlineFunction - test 11", () => {
    expect(tokenizeInlineFunction("$$foo(\\\\$)")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 3,
          value: "\\\\$",
        },
      ],
    });
  });
  test("tokenizeInlineFunction - test 12", () => {
    expect(tokenizeInlineFunction("$$foo('\\\\$')")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 5,
          value: "\\$",
        },
      ],
    });
  });
  test("tokenizeInlineFunction - test 13", () => {
    expect(tokenizeInlineFunction("$$foo(' ')")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 3,
          value: " ",
        },
      ],
    });
  });
  test("tokenizeInlineFunction - test 14", () => {
    expect(tokenizeInlineFunction("$$foo( )")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 1,
          value: " ",
        },
      ],
    });
  });
  test("tokenizeInlineFunction - test 15", () => {
    expect(tokenizeInlineFunction("$$foo(  '  a ' ,b)")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 9,
          value: "  a ",
        },
        {
          index: 16,
          length: 1,
          value: "b",
        },
      ],
    });
  });
  test("tokenizeInlineFunction - test 16", () => {
    //await assertTransformation(null, "$$argstest('\\n\\r\\t\\u0f0f'):", "\n\r\t\u0f0f");
    // not detected
    //await assertTransformation(null, "$$argstest(\n\r\t\u0f0f)", "$$argstest(\n\r\t\u0f0f)");
    expect(tokenizeInlineFunction("$$foo('\\n\\r\\t\\u0f0f')")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 14,
          value: "\n\r\t\u0f0f",
        },
      ],
    });
  });
  test("tokenizeInlineFunction - test 17", () => {
    expect(tokenizeInlineFunction("$$foo(\n\r\t\u0f0f)")).toEqual({
      name: "foo",
      keyLength: 5,
      args: [
        {
          index: 6,
          length: 4,
          value: "\n\r\t\u0f0f",
        },
      ],
    });
  });
});
