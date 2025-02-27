import { describe, expect, test } from "vitest";
import CsvFormat from "../../../formats/csv/CsvFormat";

describe("CsvFormat", () => {
  function assertOutputFrom(input: string, output: string, names?: string[] | null, forceQuote?: boolean | null) {
    expect(output).toEqual(new CsvFormat(names, null, forceQuote, null).serialize(JSON.parse(input)));
  }

  test("testCsv", () => {
    assertOutputFrom(
      `
      [{"a":"A","b":1},{"a":"C","b":2}]
    `,
      `a,b
A,1
C,2
`,
    );
  });

  test("testCsv_withNames_reduced", () => {
    assertOutputFrom(`[{"a":"A","b":1},{"a":"C","b":2}]`, "a\nA\nC\n", ["a"]);
  });

  test("testCsv_arrays", () => {
    assertOutputFrom(`[[1,2],[3,4]]`, "1,2\n3,4\n");
  });

  test("testCsv_arrays_withNames", () => {
    assertOutputFrom(`[[1,2],[3,4]]`, "a,b\n1,2\n3,4\n", ["a", "b"]);
  });

  test("testCsv_escape", () => {
    assertOutputFrom(
      `
      [{"a":"Don't","b":1},{"a":"D\\"C\\"","b":"ok,"}]
    `,
      `a,b
Don't,1
"D""C""\","ok,"
`,
    );

    assertOutputFrom(
      `
      [{"a":"Don\\nt","b":1},{"a":"D\\"C\\"","b":"ok,"}]
    `,
      `a,b
"Don
t",1
"D""C""\","ok,"
`,
    );
  });

  test("testCsv_forceQuote", () => {
    assertOutputFrom(
      `
      [{"a":"A","b":1},{"a":"C","b":2}]
    `,
      `"a","b"
"A","1"
"C","2"
`,
      null,
      true,
    );
    assertOutputFrom(
      `
      [[1,2],[3,4]]
    `,
      `"a","b"
"1","2"
"3","4"
`,
      ["a", "b"],
      true,
    );
  });

  function assertDeserializationOutputFrom(
    input: string,
    output: string,
    noHeaders?: boolean | null,
    names?: string[] | null,
  ) {
    expect(JSON.parse(output)).toEqual(new CsvFormat(names, noHeaders, null, null).deserialize(input));
  }

  test("testParseCSV_default_noData", () => {
    assertDeserializationOutputFrom("a,b", "[]");
  });

  test("testParseCSV_default_oneRow", () => {
    assertDeserializationOutputFrom(
      "a,b\nA,B",
      `
      [{ "a": "A", "b": "B" }]`,
    );
  });

  test("testParseCSV_default_ignoreSpaces", () => {
    assertDeserializationOutputFrom(
      "a,b\nA,   B",
      `
      [{ "a": "A", "b": "B" }]`,
    );
    assertDeserializationOutputFrom(
      "a,b\nA,\t\tB",
      `
      [{ "a": "A", "b": "B" }]`,
    );
    assertDeserializationOutputFrom(
      'a,b\nA,"  B"',
      `
      [{ "a": "A", "b": "  B" }]`,
    );
  });

  test("testParseCSV_default_escapingComma", () => {
    assertDeserializationOutputFrom(
      'a\n","',
      `
      [{ "a": "," }]`,
    );
  });
  test("testParseCSV_default_escapingNewline", () => {
    assertDeserializationOutputFrom(
      `"aaa","b
bb","ccc"
zzz,yyy,xxx
`,
      `
      [{"aaa":"zzz","b\\nbb":"yyy","ccc":"xxx"}]`,
    );
  });
  test("testParseCSV_default_extraFields", () => {
    assertDeserializationOutputFrom(
      `a,b
1,2,3`,
      `
      [{"a":"1","b":"2","$2":"3"}]`,
    );
  });

  test("testParseCSV_default_escapingQuotes", () => {
    assertDeserializationOutputFrom(
      'a\n"a""b"',
      `
      [{ "a": "a\\"b" }]`,
    );
    assertDeserializationOutputFrom(
      'a\n"a"""',
      `
      [{ "a": "a\\"" }]`,
    );
    assertDeserializationOutputFrom(
      'a\n"""b"',
      `
      [{ "a": "\\"b" }]`,
    );
    assertDeserializationOutputFrom(
      'a\n""""',
      `
      [{ "a": "\\"" }]`,
    );
  });

  test("testParseCSV_default_possibleIssuesAtEndOfRow", () => {
    assertDeserializationOutputFrom(
      "a,b\nA,B\nC",
      `
        [{ "a": "A", "b": "B" },{ "a": "C" }]`,
    );
    assertDeserializationOutputFrom(
      "a,b\nA,B\nC,",
      `
        [{ "a": "A", "b": "B" },{ "a": "C", "b": "" }]`,
    );
    assertDeserializationOutputFrom(
      "a,b\nA,B\nC,\n",
      `
        [{ "a": "A", "b": "B" },{ "a": "C", "b": "" }]`,
    );
  });

  test("testParseCSV_noHeaders_arrays", () => {
    assertDeserializationOutputFrom(
      "a,b\nA,B",
      `
        [["a","b"],["A","B"]]`,
      true,
      null,
    );
  });
  test("testParseCSV_noHeaders_escapingComma", () => {
    assertDeserializationOutputFrom(
      '",",","',
      `
        [[",",","]]`,
      true,
      null,
    );
  });

  test("testParseCSV_noHeaders_escapingQuotes", () => {
    assertDeserializationOutputFrom(
      '"\'",""""',
      `
        [["'","\\""]]`,
      true,
      null,
    );
  });

  test("testParseCSV_noHeaders__names", () => {
    assertDeserializationOutputFrom(
      `1,2
2,4
3,6
`,
      `[{"number":"1","twice":"2"},{"number":"2","twice":"4"},{"number":"3","twice":"6"}]`,
      true,
      ["number", "twice"],
    );
  });

  test("testParseCSV_names", () => {
    assertDeserializationOutputFrom(
      `a,b,c
1,2,3
4,5,6
`,
      `[{"a":"1","b":"2"},{"a":"4","b":"5"}]`,
      false,
      ["a", "b"],
    );
  });
});
