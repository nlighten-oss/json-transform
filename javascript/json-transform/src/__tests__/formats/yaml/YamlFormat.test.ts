import { describe, expect, test } from "vitest";
import YamlFormat from "../../../formats/yaml/YamlFormat";

describe("YamlFormat", () => {
  test("simple", async () => {
    const json = { a: 1, b: 2 };
    const yaml = YamlFormat.INSTANCE.serialize(json);
    expect(yaml).toBe("a: 1\nb: 2\n");
  });

  test("complex", async () => {
    const json = { a: "1", b: ["B", 2], c: true, d: { e: ["E", "800"] } };
    const yaml = YamlFormat.INSTANCE.serialize(json);
    expect(yaml).toBe("a: '1'\nb:\n  - B\n  - 2\nc: true\nd:\n  e:\n    - E\n    - '800'\n");
  });
});
