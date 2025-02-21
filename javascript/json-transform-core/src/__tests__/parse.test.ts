import { describe, expect, test } from "vitest";
import { parseTransformer } from "../parse";
import { TypeSchema } from "@nlighten/json-schema-utils";

describe("parse", () => {
  test("should parse $name", () => {
    const prevPaths = ["$name", "$name.a"];
    const paths = ["target"];
    const typeMap: Record<string, TypeSchema> = {
      $name: { type: "object" },
      "$name.a": { type: "string" },
    };
    parseTransformer("$name", "target", prevPaths, paths, typeMap);
    // check that target paths were added
    expect(paths).toEqual(["target", "target.a"]);
    // check that types were added
    expect(Object.keys(typeMap)).toHaveLength(4);
    expect(typeMap["target"]).toEqual({ type: "object" });
    expect(typeMap["target.a"]).toEqual({ type: "string" });
  });
});
