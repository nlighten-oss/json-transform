import { expect, describe, test } from "vitest";
import { parameterResolverFromMap, TextTemplate } from "../..";
import { createPayloadResolver } from "../../JsonHelpers";

describe("TextTemplate", () => {
  test("run json transformer function", async () => {
    const resolver = createPayloadResolver({
      n: "NAME",
    });
    const def = await new TextTemplate("Hello {$$lower:$.n}").render(resolver);
    expect(def).toEqual("Hello name");
  });

  test("run json transformer function 2", async () => {
    const resolver = createPayloadResolver({
      a: ["hello", " ", "world"],
      u: "cb2aa228-b265-3f99-aae4-73a58f7be18b",
    });
    const def = await new TextTemplate("{$$join:$.a} {$$uuid(v3):$.u}").render(resolver);
    expect(def).toEqual("hello world 2c14e2de-ca8e-37b4-bf57-bed2799a6b33");
  });

  test("run unknown json transformer function", async () => {
    const resolver = createPayloadResolver({
      n: "NAME",
    });
    const def = await new TextTemplate("Hello {$$xxx:$.n}").render(resolver);
    expect(def).toEqual("Hello $$xxx:$.n");
  });

  test("recursiveWithDefaultValue", async () => {
    const resolver = parameterResolverFromMap({
      A: "A",
      b: "B",
    });
    const def = await new TextTemplate("{a,b}").render(resolver);
    expect(def).toEqual("b");
  });
  test("recursiveWithDefaultValueNestingParameters", async () => {
    const resolver = parameterResolverFromMap({
      A: "A",
      b: "B",
    });
    let def = await new TextTemplate("{a,{b}}").render(resolver);
    expect(def).toEqual("B");
    def = await new TextTemplate("{A,{b}}").render(resolver);
    expect(def).toEqual("A");
    def = await new TextTemplate("{x,{y,{b}}}").render(resolver);
    expect(def).toEqual("B");
  });

  test("escapedTemplateParameter", async () => {
    const resolver = parameterResolverFromMap({
      a: "A",
    });
    const def = await new TextTemplate("\\{a}").render(resolver);
    expect(def).toEqual("{a}");
  });

  test("resolvedTemplateParameterToTemplateLike", async () => {
    const resolver = parameterResolverFromMap({
      a: "{b}",
      b: "B",
    });
    const def = await new TextTemplate("{a}").render(resolver);
    expect(def).toEqual("{b}");
  });

  test("recursiveWithEscapedTemplateParameterDefaultValue", async () => {
    const resolver = parameterResolverFromMap({
      a: "A",
    });
    let def = await new TextTemplate("{b,{a}}").render(resolver);
    expect(def).toEqual("A");
    def = await new TextTemplate("{b,\\{a}}").render(resolver);
    expect(def).toEqual("{a}");
  });

  test("recursiveWithEscapedTemplateParameterDefaultValue2", async () => {
    const resolver = parameterResolverFromMap({
      a: "A",
      b: "{a}",
    });
    const def = await new TextTemplate("{x,{b}}").render(resolver);
    expect(def).toEqual("{a}");
  });
  test("recursiveWithEscapedTemplateParameterDefaultValue3", async () => {
    const resolver = parameterResolverFromMap({
      a: "A",
    });
    const def = await new TextTemplate("{b,\\{a}}").render(resolver);
    expect(def).toEqual("{a}");
  });

  test("urlEncode", async () => {
    const resolver = parameterResolverFromMap({
      href: "https://example.com/",
    });
    // true
    let def = await new TextTemplate("href={href}").render(resolver, true);
    expect(def).toEqual("href=https%3A%2F%2Fexample.com%2F");
    // false
    def = await new TextTemplate("href={href}").render(resolver);
    expect(def).toEqual("href=https://example.com/");
  });
});
