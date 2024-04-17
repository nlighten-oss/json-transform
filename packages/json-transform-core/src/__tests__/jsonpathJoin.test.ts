import { describe, test, expect } from "vitest";
import { jsonpathJoin } from "../jsonpath/jsonpathJoin";

describe("jsonpathJoin", () => {
  test("test 1", () => {
    const result = jsonpathJoin("$", "test");
    expect(result).toEqual("$.test");
  });
  test("test 2", () => {
    const result = jsonpathJoin("$", "test", "");
    expect(result).toEqual("$.test");
    const result2 = jsonpathJoin("$", "test", null);
    expect(result2).toEqual("$.test");
    const result3 = jsonpathJoin("$", "test", undefined);
    expect(result3).toEqual("$.test");
  });
  test("test 3", () => {
    const result = jsonpathJoin("$", "[]");
    expect(result).toEqual("$[]");
  });
  test("test 4", () => {
    const result = jsonpathJoin("$", "test", "[]");
    expect(result).toEqual("$.test[]");
  });
});
