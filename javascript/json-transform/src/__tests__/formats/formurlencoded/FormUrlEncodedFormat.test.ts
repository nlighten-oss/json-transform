import { describe, expect, test } from "vitest";
import FormUrlEncodedFormat from "../../../formats/formurlencoded/FormUrlEncodedFormat";

describe("FormUrlEncodedFormat", () => {
  class FUETest {
    title = "Hello World";
    numbers = [1, 2];
  }

  test("serialize", () => {
    const xbt = new FormUrlEncodedFormat();
    const result = xbt.serialize(new FUETest());

    const expected = "title=Hello+World&numbers=1&numbers=2";
    const expectAlt = "numbers=1&numbers=2&title=Hello+World";

    expect(expected === result || expectAlt === result).toBeTruthy();

    const test2 = new FUETest();
    test2.title = "not url+safe?=x&b=path/path";
    const result2 = xbt.serialize(test2);

    const expect2 = "title=not+url%2Bsafe%3F%3Dx%26b%3Dpath%2Fpath&numbers=1&numbers=2";
    const expect2Alt = "numbers=1&numbers=2&title=not+url%2Bsafe%3F%3Dx%26b%3Dpath%2Fpath";

    expect(expect2 === result2 || expect2Alt === result2).toBeTruthy();
  });

  test("testDeserialize", () => {
    const xbt = new FormUrlEncodedFormat();
    const result = xbt.deserialize("a=1&b=hello&c");
    expect({
      a: "1",
      a$$: ["1"],
      b: "hello",
      b$$: ["hello"],
      c: "true",
      c$$: ["true"],
    }).toMatchObject(result);

    const result2 = xbt.deserialize("a=one&b=a&b=b&b=c");
    expect({
      a: "one",
      a$$: ["one"],
      b: "a",
      b$$: ["a", "b", "c"],
    }).toMatchObject(result2);

    const result3 = xbt.deserialize("c&c&d=Hello+World&title=not+url%2Bsafe%3F%3Dx%26b%3Dpath%2Fpath");
    expect({
      c: "true",
      c$$: ["true", "true"],
      d: "Hello World",
      d$$: ["Hello World"],
      title: "not url+safe?=x&b=path/path",
      title$$: ["not url+safe?=x&b=path/path"],
    }).toMatchObject(result3);
  });
});
