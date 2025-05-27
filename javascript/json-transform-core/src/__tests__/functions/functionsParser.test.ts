import { describe, expect, test } from "vitest";
import { cleanParsedSchemaProperty, type TypeSchema } from "@nlighten/json-schema-utils";
import { EmbeddedTransformerFunction, EmbeddedTransformerFunctions } from "../../functions/types";
import { parseTransformer } from "../../parse";
import { functionsParser } from "../../functions/functionsParser";

const transformerResult = (
  transformer: Record<string, string | Record<string, any>>,
  givenTypeMap?: Record<string, TypeSchema>,
) => {
  const paths: string[] = [];
  const outputTypeMap: Record<string, TypeSchema> = structuredClone(givenTypeMap) ?? {};
  const previousPaths = givenTypeMap ? Object.keys(givenTypeMap) : [];
  parseTransformer(transformer, "$", previousPaths, paths, outputTypeMap);
  return outputTypeMap;
};
function removeUndefined<T>(obj: Record<string, T>): Record<string, T> {
  return Object.fromEntries(Object.entries(obj).filter(x => typeof x[1] !== "undefined"));
}

const createFlowTraversalResult = (merge: { paths: Record<string, any> }) =>
  merge?.paths ? removeUndefined(merge.paths) : {};

const NULL: TypeSchema = { type: "null" },
  ARRAY: TypeSchema = { type: "array" },
  STRING: TypeSchema = { type: "string" },
  INTEGER: TypeSchema = { type: "integer" },
  LONG: TypeSchema = { type: "integer", $comment: "Long" },
  NUMBER: TypeSchema = { type: "number" },
  BOOLEAN: TypeSchema = { type: "boolean" },
  OBJECT: TypeSchema = { type: "object" },
  ARRAY_ARRAY: TypeSchema = { type: "array", items: ARRAY }, // eslint-disable-line @typescript-eslint/no-unused-vars
  STRING_ARRAY: TypeSchema = { type: "array", items: STRING }, // eslint-disable-line @typescript-eslint/no-unused-vars
  INTEGER_ARRAY: TypeSchema = { type: "array", items: INTEGER },
  NUMBER_ARRAY: TypeSchema = { type: "array", items: NUMBER },
  BOOLEAN_ARRAY: TypeSchema = { type: "array", items: BOOLEAN }, // eslint-disable-line @typescript-eslint/no-unused-vars
  OBJECT_ARRAY: TypeSchema = { type: "array", items: OBJECT };

describe("functions schema detection", () => {
  for (const funcName of EmbeddedTransformerFunctions) {
    const func = functionsParser.get(funcName);
    const alias = `$$${funcName}`;
    const outputSchema = func.outputSchema;

    // if (funcName !== TransformerFunction.transform) continue; // This can make the tests run only one for easy debugging

    test(`functions - ${funcName} - outputSchema detection`, () => {
      switch (funcName as EmbeddedTransformerFunction) {
        case EmbeddedTransformerFunction.coalesce: {
          const givenTypeMap: Record<string, TypeSchema> = {
            "$.a0": NULL,
            "$.a1": INTEGER,
            "$.a2": NUMBER_ARRAY,
            "$.a2[]": NUMBER,
          };
          const altAlias = `$$${functionsParser.get(funcName).aliases?.[0]}`;
          expect(
            transformerResult(
              {
                inline: `${alias}:$.a2`,
                inlineAlias: `${altAlias}:$.a2`,
                object: {
                  [alias]: [null, null, "A"],
                },
                objectAlias: {
                  [altAlias]: [null, null, "A"],
                },
                object2: {
                  [alias]: [null, null, 0.5],
                },
                object3: {
                  [alias]: ["$.a0", "$.a1"],
                },
              },
              givenTypeMap,
            ),
          ).toStrictEqual(
            createFlowTraversalResult({
              paths: {
                $: {
                  additionalProperties: false,
                  properties: {
                    inline: NUMBER,
                    inlineAlias: NUMBER,
                    object: STRING,
                    objectAlias: STRING,
                    object2: NUMBER,
                    object3: INTEGER,
                  },
                  type: "object",
                },
                ...givenTypeMap,
                "$.inline": NUMBER,
                "$.inlineAlias": NUMBER,
                "$.object": STRING,
                "$.objectAlias": STRING,
                "$.object2": NUMBER,
                "$.object3": INTEGER,
              },
            }),
          );
          break;
        }
        case EmbeddedTransformerFunction.at:
        case EmbeddedTransformerFunction.concat:
        case EmbeddedTransformerFunction.find:
        case EmbeddedTransformerFunction.flat: {
          const givenTypeMap: Record<string, TypeSchema> = {
            "$.a0": { type: "array", items: NUMBER_ARRAY },
            "$.a0[]": NUMBER_ARRAY,
            "$.a0[][]": NUMBER,
            "$.a1": INTEGER_ARRAY,
            "$.a1[0]": INTEGER,
            "$.a2": INTEGER_ARRAY,
            "$.a2[]": INTEGER,
          };
          expect(
            transformerResult(
              {
                inline: `${alias}:$.a0`,
                object: {
                  [alias]: ["$.a1", "$.a2"],
                },
              },
              givenTypeMap,
            ),
          ).toStrictEqual(
            createFlowTraversalResult({
              paths: {
                $: {
                  additionalProperties: false,
                  properties: {
                    inline: NUMBER_ARRAY,
                    object: INTEGER_ARRAY,
                  },
                  type: "object",
                },
                ...givenTypeMap,
                "$.inline": NUMBER_ARRAY,
                "$.inline[]": NUMBER,
                "$.object": INTEGER_ARRAY,
                "$.object[0]": INTEGER,
              },
            }),
          );
          break;
        }
        case EmbeddedTransformerFunction.if: {
          expect(
            transformerResult({
              inline: `${alias}(A,B):$.a0`,
              inline2: `${alias}($$long:1,$$long:2):$.a0`,
              object: {
                [alias]: [false, "A", "B"],
              },
              object2: {
                [alias]: [false, 1, 2],
              },
              object3: {
                [alias]: false,
                then: 0.5,
              },
            }),
          ).toStrictEqual(
            createFlowTraversalResult({
              paths: {
                $: {
                  additionalProperties: false,
                  properties: {
                    inline: STRING,
                    inline2: LONG,
                    object: STRING,
                    object2: INTEGER,
                    object3: NUMBER,
                  },
                  type: "object",
                },
                "$.inline": STRING,
                "$.inline2": LONG,
                "$.object": STRING,
                "$.object2": INTEGER,
                "$.object3": NUMBER,
              },
            }),
          );
          break;
        }
        case EmbeddedTransformerFunction.distinct:
        case EmbeddedTransformerFunction.filter:
        case EmbeddedTransformerFunction.reverse:
        case EmbeddedTransformerFunction.slice:
        case EmbeddedTransformerFunction.sort: {
          const givenTypeMap: Record<string, TypeSchema> = {
            "$.a0": NUMBER_ARRAY,
            "$.a0[]": NUMBER,
            "$.a1": INTEGER,
            "$.a2": INTEGER,
          };
          expect(
            transformerResult(
              {
                inline: `${alias}:$.a0`,
                object: {
                  [alias]: "$.a0",
                },
                object2: {
                  [alias]: ["$.a1", "$.a2"],
                },
              },
              givenTypeMap,
            ),
          ).toStrictEqual(
            createFlowTraversalResult({
              paths: {
                $: {
                  additionalProperties: false,
                  properties: {
                    inline: NUMBER_ARRAY,
                    object: NUMBER_ARRAY,
                    object2: INTEGER_ARRAY,
                  },
                  type: "object",
                },
                ...givenTypeMap,
                "$.inline": NUMBER_ARRAY,
                "$.inline[]": NUMBER,
                "$.object": NUMBER_ARRAY,
                "$.object[]": NUMBER,
                "$.object2": INTEGER_ARRAY,
                "$.object2[]": INTEGER,
              },
            }),
          );
          break;
        }
        // we don't infer output type :_(
        case EmbeddedTransformerFunction.eval: {
          expect(
            transformerResult({
              inline: `${alias}:irrelevant`,
              object: {
                [alias]: "irrelevant",
              },
            }),
          ).toStrictEqual(
            createFlowTraversalResult({
              paths: {
                $: {
                  additionalProperties: false,
                  type: "object",
                },
              },
            }),
          );
          break;
        }
        case EmbeddedTransformerFunction.repeat: {
          const givenTypeMap: Record<string, TypeSchema> = {
            "$.a0": INTEGER_ARRAY,
            "$.a0[]": INTEGER,
            "$.a1": STRING,
          };
          expect(
            transformerResult(
              {
                inline: `${alias}(irrelevant):$.a0`,
                inline2: `${alias}(irrelevant):$.a1`,
                object: {
                  [alias]: "$.a0",
                },
              },
              givenTypeMap,
            ),
          ).toStrictEqual(
            createFlowTraversalResult({
              paths: {
                $: {
                  additionalProperties: false,
                  properties: {
                    inline: { type: "array", items: INTEGER_ARRAY },
                    inline2: STRING_ARRAY,
                    object: { type: "array", items: INTEGER_ARRAY },
                  },
                  type: "object",
                },
                ...givenTypeMap,
                "$.inline": { type: "array", items: INTEGER_ARRAY },
                "$.inline[]": INTEGER_ARRAY,
                "$.inline[][]": INTEGER,
                "$.inline2": STRING_ARRAY,
                "$.inline2[]": STRING,
                "$.object": { type: "array", items: INTEGER_ARRAY },
                "$.object[]": INTEGER_ARRAY,
                "$.object[][]": INTEGER,
              },
            }),
          );
          break;
        }
        case EmbeddedTransformerFunction.lookup: {
          const givenTypeMap: Record<string, TypeSchema> = {
            "$.a0": ARRAY,
            "$.a0[]": OBJECT,
            "$.a0[].id": NUMBER,
            "$.a0[].name": STRING,
            "$.a1": ARRAY,
            "$.a1[]": OBJECT,
            "$.a1[].id": NUMBER,
            "$.a1[].age": INTEGER,
          };
          expect(
            transformerResult(
              {
                //inline: `${alias}:$.a0`,
                object: {
                  [alias]: "$.a0",
                  using: [{ with: "$.a1", as: "match", on: {} }],
                },
                object2: {
                  [alias]: "$.a0",
                  using: [{ with: "$.a1", as: "match", on: {} }],
                  to: {
                    name: "##current.name",
                    age: "##match.age",
                  },
                },
              },
              givenTypeMap,
            ),
          ).toStrictEqual(
            createFlowTraversalResult({
              paths: {
                $: {
                  additionalProperties: false,
                  properties: {
                    object: OBJECT_ARRAY,
                    object2: {
                      type: "array",
                      items: {
                        type: "object",
                        properties: {
                          age: INTEGER,
                          name: STRING,
                        },
                        additionalProperties: false,
                      },
                    },
                  },
                  type: "object",
                },
                ...givenTypeMap,
                "$.object": OBJECT_ARRAY,
                "$.object[]": OBJECT,
                "$.object[].id": NUMBER,
                "$.object[].age": INTEGER,
                "$.object[].name": STRING,
                "$.object2": {
                  type: "array",
                  items: {
                    type: "object",
                    properties: {
                      age: INTEGER,
                      name: STRING,
                    },
                    additionalProperties: false,
                  },
                },
                "$.object2[]": {
                  type: "object",
                  properties: {
                    age: INTEGER,
                    name: STRING,
                  },
                  additionalProperties: false,
                },
                "$.object2[].age": INTEGER,
                "$.object2[].name": STRING,
              },
            }),
          );
          break;
        }
        case EmbeddedTransformerFunction.min:
        case EmbeddedTransformerFunction.max: {
          const givenTypeMap: Record<string, TypeSchema> = {
            "$.a0": INTEGER_ARRAY,
            "$.a0[]": INTEGER,
          };
          expect(
            transformerResult(
              {
                inline: `${alias}:$.a0`,
                inline_str: `${alias}(0,STRING):$.a0`,
                inline_num: `${alias}(0,NUMBER):$.a0`,
                inline_bool: `${alias}(0,BOOLEAN):$.a0`,
                object: {
                  [alias]: "$.a0",
                },
                object_str: {
                  [alias]: "$.a0",
                  type: "STRING",
                },
                object_num: {
                  [alias]: "$.a0",
                  type: "NUMBER",
                },
                object_bool: {
                  [alias]: "$.a0",
                  type: "BOOLEAN",
                },
              },
              givenTypeMap,
            ),
          ).toStrictEqual(
            createFlowTraversalResult({
              paths: {
                $: {
                  additionalProperties: false,
                  properties: {
                    inline: INTEGER,
                    inline_str: STRING,
                    inline_num: NUMBER,
                    inline_bool: BOOLEAN,
                    object: INTEGER,
                    object_str: STRING,
                    object_num: NUMBER,
                    object_bool: BOOLEAN,
                  },
                  type: "object",
                },
                ...givenTypeMap,
                "$.inline": INTEGER,
                "$.inline_str": STRING,
                "$.inline_num": NUMBER,
                "$.inline_bool": BOOLEAN,
                "$.object": INTEGER,
                "$.object_str": STRING,
                "$.object_num": NUMBER,
                "$.object_bool": BOOLEAN,
              },
            }),
          );
          break;
        }
        case EmbeddedTransformerFunction.merge: {
          const givenTypeMap: Record<string, TypeSchema> = {
            "$.a0": OBJECT,
            "$.a0.id": NUMBER,
            "$.a0.name": OBJECT,
            "$.a0.name.first_name": STRING,
            "$.a1": OBJECT,
            "$.a1.id": NUMBER,
            "$.a1.age": INTEGER,
            "$.a1.name": OBJECT,
            "$.a1.name.last_name": STRING,
          };
          expect(
            transformerResult(
              {
                //inline: `${alias}:$.a0`,
                object: {
                  [alias]: ["$.a0", "$.a1"],
                  deep: true,
                },
                object2: {
                  [alias]: ["$.a0", "$.a1"],
                },
              },
              givenTypeMap,
            ),
          ).toStrictEqual(
            createFlowTraversalResult({
              paths: {
                $: {
                  additionalProperties: false,
                  properties: {
                    object: OBJECT,
                    object2: OBJECT,
                  },
                  type: "object",
                },
                ...givenTypeMap,
                "$.object": OBJECT,
                "$.object.id": NUMBER,
                "$.object.age": INTEGER,
                "$.object.name": OBJECT,
                "$.object.name.first_name": STRING,
                "$.object.name.last_name": STRING,
                "$.object2": OBJECT,
                "$.object2.id": NUMBER,
                "$.object2.age": INTEGER,
                "$.object2.name": OBJECT,
                "$.object2.name.first_name": STRING, // TODO: should not be here
                "$.object2.name.last_name": STRING,
              },
            }),
          );
          break;
        }
        case EmbeddedTransformerFunction.map: {
          const givenTypeMap: Record<string, TypeSchema> = {
            "$.a0": { type: "array", items: INTEGER_ARRAY },
            "$.a0[]": INTEGER_ARRAY,
            "$.a0[][]": INTEGER,
          };
          expect(
            transformerResult(
              {
                inline: `${alias}(##current[0]):$.a0`,
                object: {
                  [alias]: "$.a0",
                  to: "##current[0]",
                },
              },
              givenTypeMap,
            ),
          ).toStrictEqual(
            createFlowTraversalResult({
              paths: {
                $: {
                  additionalProperties: false,
                  properties: {
                    inline: INTEGER_ARRAY,
                    object: INTEGER_ARRAY,
                  },
                  type: "object",
                },
                ...givenTypeMap,
                "$.inline": INTEGER_ARRAY,
                "$.inline[]": INTEGER,
                "$.object": INTEGER_ARRAY,
                "$.object[]": INTEGER,
              },
            }),
          );
          break;
        }
        case EmbeddedTransformerFunction.partition: {
          const givenTypeMap: Record<string, TypeSchema> = {
            "$.a0": { type: "array", items: NUMBER },
            "$.a0[]": NUMBER,
          };
          expect(
            transformerResult(
              {
                inline: `${alias}:$.a0`,
                object: {
                  [alias]: "$.a0",
                },
              },
              givenTypeMap,
            ),
          ).toStrictEqual(
            createFlowTraversalResult({
              paths: {
                $: {
                  additionalProperties: false,
                  properties: {
                    inline: { type: "array", items: NUMBER_ARRAY },
                    object: { type: "array", items: NUMBER_ARRAY },
                  },
                  type: "object",
                },
                ...givenTypeMap,
                "$.inline": { type: "array", items: NUMBER_ARRAY },
                "$.inline[]": NUMBER_ARRAY,
                "$.inline[][]": NUMBER,
                "$.object": { type: "array", items: NUMBER_ARRAY },
                "$.object[]": NUMBER_ARRAY,
                "$.object[][]": NUMBER,
              },
            }),
          );
          break;
        }
        case EmbeddedTransformerFunction.transform: {
          const givenTypeMap: Record<string, TypeSchema> = {
            "$.a0": INTEGER_ARRAY,
            "$.a0[]": INTEGER,
          };
          expect(
            transformerResult(
              {
                inline: `${alias}(##current[0]):$.a0`,
                object: {
                  [alias]: "$.a0",
                  to: "##current[0]",
                },
              },
              givenTypeMap,
            ),
          ).toStrictEqual(
            createFlowTraversalResult({
              paths: {
                $: {
                  additionalProperties: false,
                  properties: {
                    inline: INTEGER,
                    object: INTEGER,
                  },
                  type: "object",
                },
                ...givenTypeMap,
                "$.inline": INTEGER,
                "$.object": INTEGER,
              },
            }),
          );
          break;
        }
        case EmbeddedTransformerFunction.reduce: {
          const givenTypeMap: Record<string, TypeSchema> = {
            "$.a0": { type: "array", items: { type: "object", properties: { amount: NUMBER } } },
            "$.a0[]": { type: "object", properties: { amount: NUMBER } },
            "$.a0[].number": NUMBER,
          };
          expect(
            transformerResult(
              {
                inline: `${alias}('$$math(##accumulator,+,##current.amount)',$$long:0):$.a0`,
                object: {
                  [alias]: "$.a0",
                  identity: 0.5,
                  to: {},
                },
              },
              givenTypeMap,
            ),
          ).toStrictEqual(
            createFlowTraversalResult({
              paths: {
                $: {
                  additionalProperties: false,
                  properties: {
                    inline: LONG,
                    object: NUMBER,
                  },
                  type: "object",
                },
                ...givenTypeMap,
                "$.inline": LONG,
                "$.object": NUMBER,
              },
            }),
          );
          break;
        }
        case EmbeddedTransformerFunction.switch: {
          expect(
            transformerResult({
              object: {
                [alias]: "$.a0",
                cases: { a: 1, B: 2, c: 3 },
              },
              object2: {
                [alias]: "$.a0",
                cases: { a: 1, B: 2, c: 3 },
                default: 0.5,
              },
            }),
          ).toStrictEqual(
            createFlowTraversalResult({
              paths: {
                $: {
                  additionalProperties: false,
                  properties: {
                    object: INTEGER,
                    object2: NUMBER,
                  },
                  type: "object",
                },
                "$.object": INTEGER,
                "$.object2": NUMBER,
              },
            }),
          );
          break;
        }
        case EmbeddedTransformerFunction.raw:
        case EmbeddedTransformerFunction.jsonpatch:
        case EmbeddedTransformerFunction.value: {
          const givenTypeMap: Record<string, TypeSchema> = {
            "$.a0": INTEGER_ARRAY,
            "$.a0[]": INTEGER,
          };
          expect(
            transformerResult(
              {
                inline: `${alias}:$.a0`,
                object: {
                  [alias]: "$.a0",
                },
              },
              givenTypeMap,
            ),
          ).toStrictEqual(
            createFlowTraversalResult({
              paths: {
                $: {
                  additionalProperties: false,
                  properties: {
                    inline: funcName === EmbeddedTransformerFunction.raw ? STRING : INTEGER_ARRAY,
                    object: funcName === EmbeddedTransformerFunction.raw ? STRING : INTEGER_ARRAY,
                  },
                  type: "object",
                },
                ...givenTypeMap,
                "$.inline": funcName === EmbeddedTransformerFunction.raw ? STRING : INTEGER_ARRAY,
                "$.inline[]": funcName === EmbeddedTransformerFunction.raw ? (undefined as any) : INTEGER,
                "$.object": funcName === EmbeddedTransformerFunction.raw ? STRING : INTEGER_ARRAY,
                "$.object[]": funcName === EmbeddedTransformerFunction.raw ? (undefined as any) : INTEGER,
              },
            }),
          );
          break;
        }
        // static output schema
        default: {
          if (func.subfunctions) {
            // static conditional
            const paramName = func.arguments?.find(p => p.position === 0)?.name ?? "";
            const expected: Record<string, TypeSchema> = {};
            const outputProperties: Record<string, TypeSchema> = {};
            const transformer = func.subfunctions.reduce((a, kv) => {
              const argValue = kv.if[0].equals; // TODO: this is not generic
              a["inline_" + argValue] = `${alias}(${argValue}):irrelevant`;
              a["object_" + argValue] = {
                [alias]: "irrelevant",
                [paramName]: argValue,
              };
              const argOutputSchema = kv.then.outputSchema;
              if (argOutputSchema) {
                expected["$.inline_" + argValue] = argOutputSchema;
                outputProperties["inline_" + argValue] = argOutputSchema;
                expected["$.object_" + argValue] = argOutputSchema;
                outputProperties["object_" + argValue] = argOutputSchema;
                if (
                  argOutputSchema.type === "array" &&
                  argOutputSchema.items &&
                  !Array.isArray(argOutputSchema.items)
                ) {
                  const firstLevelItems = argOutputSchema.items;
                  expected["$.inline_" + argValue + "[]"] = firstLevelItems;
                  expected["$.object_" + argValue + "[]"] = firstLevelItems;
                  if (
                    firstLevelItems.type === "array" &&
                    firstLevelItems.items &&
                    !Array.isArray(firstLevelItems.items)
                  ) {
                    expected["$.inline_" + argValue + "[][]"] = firstLevelItems.items;
                    expected["$.object_" + argValue + "[][]"] = firstLevelItems.items;
                  }
                }
              }
              return a;
            }, {} as any);
            expect({ transformer, result: transformerResult(transformer) }).toStrictEqual({
              transformer,
              result: createFlowTraversalResult({
                paths: {
                  $: {
                    additionalProperties: false,
                    properties: outputProperties,
                    type: "object",
                  },
                  ...expected,
                },
              }),
            });
            break;
          }

          // static output schema
          expect(outputSchema).toBeTruthy();
          const expected: Record<string, TypeSchema> = {};
          const funcOutputPaths = functionsParser.get(funcName).parsedOutputSchema?.paths ?? [];

          funcOutputPaths.forEach(p => {
            const suffix = !p.$path ? "" : (p.$path[0] === "[" ? "" : ".") + p.$path;
            expected["$.inline" + suffix] = cleanParsedSchemaProperty(p);
            expected["$.object" + suffix] = cleanParsedSchemaProperty(p);
          });
          expect(
            transformerResult({
              inline: `${alias}:irrelevant`,
              object: {
                [alias]: "irrelevant",
              },
            }),
          ).toStrictEqual(
            createFlowTraversalResult({
              paths: {
                $: {
                  additionalProperties: false,
                  properties: {
                    inline: outputSchema,
                    object: outputSchema,
                  },
                  type: "object",
                },
                ...expected,
              },
            }),
          );
          break;
        }
      }
    });
  }
});

describe("matchAllInlineFunctionsInLine", () => {
  test(`sanity`, () => {
    expect(functionsParser.matchAllInlineFunctionsInLine("  : \"$$foo(1,2):$$bar(abc,'def'):$$\"   ")).toEqual([
      {
        name: "foo",
        keyLength: 5,
        args: [
          {
            index: 11,
            length: 1,
            value: "1",
          },
          {
            index: 13,
            length: 1,
            value: "2",
          },
        ],
        index: 5,
        input: {
          index: 16,
          length: 19,
          value: "$$bar(abc,'def'):$$",
        },
      },
      {
        keyLength: 5,
        name: "bar",
        args: [
          {
            index: 22,
            length: 3,
            value: "abc",
          },
          {
            index: 26,
            length: 5,
            value: "def",
          },
        ],
        index: 16,
        input: {
          index: 33,
          length: 6,
          value: '$$"   ',
        },
      },
    ]);
  });

  test(`sanity 2`, () => {
    expect(
      functionsParser.matchAllInlineFunctionsInLine(
        `  "age": "$$math('$$math(\\\\'$$date(EPOCH):#now\\\\',-,\\\\'$$date(EPOCH):$.date_of_birth\\\\')',//,'$$math(365,*,\\\\'$$math(24,*,3600)\\\\')')"`,
      ),
    ).toEqual([
      {
        args: [
          {
            index: 17,
            length: 72,
            value: "$$math(\\'$$date(EPOCH):#now\\',-,\\'$$date(EPOCH):$.date_of_birth\\')",
          },
          {
            index: 90,
            length: 2,
            value: "//",
          },
          {
            index: 93,
            length: 39,
            value: "$$math(365,*,\\'$$math(24,*,3600)\\')",
          },
        ],
        index: 10,
        keyLength: 6,
        name: "math",
      },
    ]);
  });
});
