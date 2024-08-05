import {describe, expect, test} from "vitest";
import DocumentContext from "../DocumentContext";

describe("DocumentContext", () => {
  test("Query root", () => {
    const obj = {x: 1};
    const dc = new DocumentContext(obj)
    expect(dc.read("$")).toBe(obj)
  });
  test("Query definite path", () => {
    const obj = {x: 1};
    const dc = new DocumentContext(obj)
    expect(dc.read("$.x")).toEqual(1)
  });
  test("Query indefinite path", () => {
    const obj = {x: [{a: 1}, {a: 2}]};
    const dc = new DocumentContext(obj)
    expect(dc.read("$..a")).toEqual([1, 2])
  });
  test("Query indefinite path 2", () => {
    const obj = {x: [{a: 1}, {a: 2}]};
    const dc = new DocumentContext(obj)
    expect(dc.read("$.x[*]")).toEqual([{a: 1}, {a: 2}])
  });
  test("Query indefinite path 2", () => {
    const obj = {x: [{a: 1}, {a: 2}]};
    const dc = new DocumentContext(obj)
    expect(dc.read("$.x[?(@.a == 2)]")).toEqual([{a: 2}])
  });
});