import { describe, test, expect } from "vitest";
import javaSplit from "../../../functions/utils/javaSplit";

describe("javaSplit", () => {
  const input = "boo:and:foo";
  test("regex : limit 2", () => {
    expect(javaSplit(input, ":", 2)).toEqual(["boo", "and:foo"]);
  });
  test("regex : limit 5", () => {
    expect(javaSplit(input, ":", 5)).toEqual(["boo", "and", "foo"]);
  });
  test("regex : limit -2", () => {
    expect(javaSplit(input, ":", -2)).toEqual(["boo", "and", "foo"]);
  });
  test("regex o limit 5", () => {
    expect(javaSplit(input, "o", 5)).toEqual(["b", "", ":and:f", "", ""]);
  });
  test("regex o limit -2", () => {
    expect(javaSplit(input, "o", -2)).toEqual(["b", "", ":and:f", "", ""]);
  });
  test("regex o limit 0", () => {
    expect(javaSplit(input, "o", 0)).toEqual(["b", "", ":and:f"]);
  });
});
