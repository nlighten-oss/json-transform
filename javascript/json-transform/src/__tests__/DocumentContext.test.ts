import { describe, expect, test } from "vitest";
import DocumentContext from "../DocumentContext";
import TextEncoding from "../functions/common/TextEncoding";

describe("DocumentContext", () => {
  test("asda", () => {
    const X =
      "hello%FE%FF%00%2Bunicode%FE%FF%00%2B%D8%3D%DE%00%D8%3D%DC%68%20%0D%D8%3D%DC%69%20%0D%D8%3D%DC%67%20%0D%D8%3D%DC%66%D8%3E%DE%AC";
    const u = new URLSearchParams("?x=" + X);
    expect(u.get("x")).toBe("hello+unicode+😀👨‍👩‍👧‍👦🪬");
    ///    expect(TextEncoding.decode(u8a, "UTF-16")).toBe("hello+unicode+😀👨‍👩‍👧‍👦🪬");
  });

  test("Query root", () => {
    const obj = { x: 1 };
    const dc = new DocumentContext(obj);
    expect(dc.read("$")).toBe(obj);
  });
  test("Query definite path", () => {
    const obj = { x: 1 };
    const dc = new DocumentContext(obj);
    expect(dc.read("$.x")).toEqual(1);
  });
  test("Query indefinite path", () => {
    const obj = { x: [{ a: 1 }, { a: 2 }] };
    const dc = new DocumentContext(obj);
    expect(dc.read("$..a")).toEqual([1, 2]);
  });
  test("Query indefinite path 2", () => {
    const obj = { x: [{ a: 1 }, { a: 2 }] };
    const dc = new DocumentContext(obj);
    expect(dc.read("$.x[*]")).toEqual([{ a: 1 }, { a: 2 }]);
  });
  test("Query indefinite path 2", () => {
    const obj = { x: [{ a: 1 }, { a: 2 }] };
    const dc = new DocumentContext(obj);
    expect(dc.read("$.x[?(@.a == 2)]")).toEqual([{ a: 2 }]);
  });
});
