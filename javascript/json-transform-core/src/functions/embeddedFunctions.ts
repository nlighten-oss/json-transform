import parseSchemas from "./parseSchemas";
import { EmbeddedTransformerFunction, FunctionDescriptor } from "./types";

export const CsvParseFunctionArgBasedSchemas: Record<string, FunctionDescriptor> = parseSchemas({
  TRUE: {
    outputSchema: { type: "array", items: { type: "array" } },
    description: "Converts a CSV string into an array of objects/arrays",
    arguments: [
      {
        name: "true",
        description: "Whether to treat the first row as object keys",
        type: "boolean",
        position: 0,
        required: true,
      },
    ],
  },
  "": {
    outputSchema: { type: "array", items: { type: "object" } },
    description: "Converts a CSV string into an array of objects/arrays",
    arguments: [
      {
        name: "names",
        description:
          "Names of fields of input arrays (by indices) or objects (can sift if provided less names than there are in the objects provided)",
        type: "array",
        position: 2,
      },
      {
        name: "no_headers",
        description: "Whether to treat the first row as object keys",
        type: "boolean",
        position: 0,
        default: false,
      },
      {
        name: "separator",
        description: "Use an alternative field separator",
        type: "string",
        position: 1,
        default: ",",
      },
    ],
  },
});

export const DateFunctionArgBasedSchemas: Record<string, FunctionDescriptor> = parseSchemas({
  DATE: {
    outputSchema: { type: "string", format: "date" },
    description: "Date part of ISO 8601",
    arguments: [
      {
        name: "DATE",
        type: "enum",
        enum: ["DATE"],
        position: 0,
        required: true,
      },
    ],
  },
  GMT: {
    outputSchema: { type: "string" },
    description: "RFC-1123 format",
    arguments: [
      {
        name: "GMT",
        type: "enum",
        enum: ["GMT"],
        position: 0,
      },
    ],
  },
  FORMAT: {
    outputSchema: { type: "string" },
    description: "Format using a date format pattern (Java style)",
    arguments: [
      {
        name: "FORMAT",
        type: "enum",
        enum: ["FORMAT"],
        position: 0,
        required: true,
      },
      { name: "pattern", description: "(FORMAT) Pattern to use", type: "string", position: 1, required: true },
      { name: "timezone", description: "(FORMAT)", type: "string", position: 2, default: "UTC" },
    ],
  },
  EPOCH: {
    outputSchema: { type: "integer" },
    description: "Seconds passed since 1970-01-01; unless `resolution`=`MS` then milliseconds,",
    arguments: [
      {
        name: "EPOCH",
        type: "enum",
        enum: ["EPOCH"],
        position: 0,
        required: true,
      },
      {
        name: "resolution",
        description: "(EPOCH) Resolution of epoch (Seconds or Milliseconds)",
        type: "enum",
        enum: ["UNIX", "MS"],
        position: 1,
        default: "UNIX",
      },
    ],
  },
  ADD: {
    outputSchema: { type: "string", format: "date-time" },
    description: "Adds chronological units from input date",
    arguments: [
      {
        name: "ADD",
        type: "enum",
        enum: ["ADD"],
        position: 0,
        required: true,
      },
      {
        name: "units",
        description: "(ADD/SUB) Units to use (ChronoUnit)",
        type: "enum",
        enum: ["NANOS", "MICROS", "MILLIS", "SECONDS", "MINUTES", "HOURS", "HALF_DAYS", "DAYS", "MONTHS", "YEARS"],
        position: 1,
        required: true,
      },
      {
        name: "amount",
        description: "(ADD/SUB) Amount of units to add or subtract (can be negative)",
        type: "long",
        position: 2,
        default: 0,
      },
    ],
  },
  SUB: {
    outputSchema: { type: "string", format: "date-time" },
    description: "Subtracts chronological units from input date",
    arguments: [
      {
        name: "SUB",
        type: "enum",
        enum: ["SUB"],
        position: 0,
        required: true,
      },
      {
        name: "units",
        description: "(ADD/SUB) Units to use (ChronoUnit)",
        type: "enum",
        enum: ["NANOS", "MICROS", "MILLIS", "SECONDS", "MINUTES", "HOURS", "HALF_DAYS", "DAYS", "MONTHS", "YEARS"],
        position: 1,
        required: true,
      },
      {
        name: "amount",
        description: "(ADD/SUB) Amount of units to add or subtract (can be negative)",
        type: "long",
        position: 2,
        default: 0,
      },
    ],
  },
  ZONE: {
    outputSchema: { type: "string", format: "date-time" },
    description: "Returns the ISO-8601 with offset by specifying a timezone",
    arguments: [
      {
        name: "ZONE",
        type: "enum",
        enum: ["ZONE"],
        position: 0,
        required: true,
      },
      { name: "zone", description: "(ZONE)", type: "string", position: 1, default: "UTC" },
    ],
  },
  "": {
    outputSchema: { type: "string", format: "date-time" },
    description:
      "Returns the ISO-8601 representation of the input date (to the specified precision set by `digits`, default is -1; maximum)",
    arguments: [
      {
        name: "ISO",
        type: "enum",
        enum: ["ISO"],
        position: 0,
        required: true,
      },
      {
        name: "digits",
        description: "(ISO) precision for time part (scale) 0|3|-1",
        type: "integer",
        position: 1,
        default: -1,
      },
    ],
  },
});

export const DigestFunctionArgBasedSchemas: Record<string, FunctionDescriptor> = parseSchemas({
  JAVA: {
    outputSchema: { type: "integer" },
    description: "Creates a message digest based on Java's hashCode()",
    arguments: [
      {
        name: "JAVA",
        type: "enum",
        enum: ["JAVA"],
        position: 0,
        required: true,
      },
    ],
  },
  "": {
    outputSchema: { type: "string" },
    description: "Creates a message digest based on a supported algorithm",
    arguments: [
      {
        name: "format",
        description:
          'Format of output (BASE64 \u003d "The Base64 Alphabet" from RFC-2045, BAS64URL \u003d "URL and Filename safe Base64 Alphabet" from RFC-4648, HEX \u003d Hexadecimal string)',
        type: "enum",
        enum: ["BASE64", "BASE64URL", "HEX"],
        position: 1,
        default: "BASE64",
      },
      {
        name: "algorithm",
        description: "Hashing algorithm",
        type: "enum",
        enum: ["SHA-1", "SHA-256", "SHA-384", "SHA-512", "MD5", "JAVA"],
        position: 0,
        default: "SHA-1",
      },
    ],
  },
});

export const embeddedFunctions: Record<EmbeddedTransformerFunction, FunctionDescriptor> = parseSchemas({
  and: {
    description: "Evaluates to `true` if all values provided will evaluate to `true` (using the [Truthy logic]).",
    inputSchema: { type: "array", description: "Values to check" },
    outputSchema: { type: "boolean" },
  },
  at: {
    description: "Retrieves an element from a specific position inside an input array",
    inputSchema: {
      type: "array",
      description: "Array to fetch from",
    },
    arguments: [
      {
        name: "index",
        description: "Index of element to fetch (negative values will be fetch from the end)",
        type: "integer",
        position: 0,
        required: true,
      },
    ],
  },
  avg: {
    description: "Returns the average of all values in the array",
    inputSchema: { type: "array" },
    outputSchema: { $comment: "BigDecimal", type: "number" },
    arguments: [
      {
        name: "default",
        description: "The default value to use for empty values",
        type: "bigdecimal",
        position: 0,
        default: 0.0,
      },
      {
        name: "by",
        description: "A transformer to extract a property to sum by (using ##current to refer to the current item)",
        type: "transformer",
        position: 1,
      },
    ],
  },
  base64: {
    description: "Encode to or decode from base64",
    inputSchema: { type: "string" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "without_padding",
        description: "Don\u0027t add padding at the end of the output (The character `\u003d`)",
        type: "boolean",
        position: 2,
        default: false,
      },
      {
        name: "action",
        description: "Whether to encode or decode input",
        type: "enum",
        position: 0,
        default: "ENCODE",
      },
      {
        name: "rfc",
        description:
          'Which alphabet to use (BASIC \u003d "The Base64 Alphabet" from RFC-2045, URL \u003d "URL and Filename safe Base64 Alphabet" from RFC-4648, MIME \u003d Same as BASIC but in lines with no more than 76 characters each)',
        type: "enum",
        position: 1,
        default: "BASIC",
      },
    ],
  },
  boolean: {
    description: "Evaluates input to boolean using the [Truthy logic]",
    notes:
      'Strings evaluation depends on `style` argument:\n- By default, value must be `"true"` for `true`.\n- Unless `style` is set to `JS`, then any non-empty value is `true`. Arrays and objects of size 0 returns `false`.\n',
    outputSchema: { type: "boolean" },
    arguments: [
      {
        name: "style",
        description: "Style of considering truthy values (JS only relates to string handling; not objects and arrays)",
        type: "enum",
        enum: ["JAVA", "JS"],
        position: 0,
        default: "JAVA",
      },
    ],
  },
  coalesce: {
    description: "Returns the first non-null value",
    inputSchema: { type: "array" },
  },
  concat: {
    description: "Concatenates primary value array with elements or other arrays of elements",
    inputSchema: { type: "array" },
  },
  contains: {
    description: "Checks whether an array contains a certain value",
    inputSchema: { type: "array" },
    outputSchema: { type: "boolean" },
    arguments: [{ name: "that", description: "The value to look for", type: "any", position: 0 }],
  },
  csv: {
    description: "Converts an array of objects/arrays to a CSV string",
    inputSchema: { type: "array" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "names",
        description:
          "Names of fields to extract into csv if objects (will be used as the header row, unless `no_headers`)",
        type: "array",
        position: 3,
      },
      {
        name: "no_headers",
        description: "Whether to include object keys as headers (taken from first object if no `names`)",
        type: "boolean",
        position: 0,
        default: false,
      },
      {
        name: "separator",
        description: "Use an alternative field separator",
        type: "string",
        position: 2,
        default: ",",
      },
      {
        name: "force_quote",
        description: "Whether to quote all the values",
        type: "boolean",
        position: 1,
        default: false,
      },
    ],
  },
  csvparse: {
    argBased: args => {
      return (
        CsvParseFunctionArgBasedSchemas[args?.no_headers?.toString().toUpperCase() ?? ""] ??
        CsvParseFunctionArgBasedSchemas[""]
      );
    },
    description: "Converts a CSV string into an array of objects/arrays",
    inputSchema: { type: "string" },
    outputSchema: { type: "array" },
    arguments: [
      {
        name: "names",
        description:
          "Names of fields of input arrays (by indices) or objects (can sift if provided less names than there are in the objects provided)",
        type: "array",
        position: 2,
      },
      {
        name: "no_headers",
        description: "Whether to treat the first row as object keys",
        type: "boolean",
        position: 0,
        default: false,
      },
      {
        name: "separator",
        description: "Use an alternative field separator",
        type: "string",
        position: 1,
        default: ",",
      },
    ],
  },
  date: {
    argBased: args => {
      if (typeof args?.format === "string") {
        return DateFunctionArgBasedSchemas[args.format.toUpperCase()] ?? DateFunctionArgBasedSchemas[""];
      }
      return DateFunctionArgBasedSchemas[""];
    },
    description: "Date formatting utility",
    notes:
      "$$date(ISO,[digits]) - ISO-8601 with specified precision, default is max\n$$date(GMT) - rfc-1123 date format\n$$date(DATE) - only the date part of ISO-8601\n$$date(ADD,{units},{amount}) - add an amount by chronological unit\n$$date(SUB,{units},{amount}) - subtract an amount by chronological unit\n$$date(EPOCH,[precision])\n$$date(FORMAT,{pattern},[timezone])\n$$date(ZONE,{zone}) - ISO-8601 with offset by specifying a timezone",
    inputSchema: { type: "string" },
    outputSchema: { type: "string", format: "date-time" },
    arguments: [
      {
        name: "amount",
        description: "(ADD/SUB) Amount of units to add or subtract (can be negative)",
        type: "long",
        position: 2,
        default: 0,
      },
      { name: "zone", description: "(ZONE)", type: "string", position: 1, default: "UTC" },
      { name: "timezone", description: "(FORMAT)", type: "string", position: 2, default: "UTC" },
      {
        name: "format",
        description: "Formatter to use",
        type: "enum",
        enum: ["ISO", "GMT", "DATE", "ADD", "SUB", "EPOCH", "FORMAT", "ZONE"],
        position: 0,
        default: "ISO",
      },
      { name: "pattern", description: "(FORMAT) Pattern to use", type: "string", position: 1, required: true },
      {
        name: "digits",
        description: "(ISO) precision for time part (scale) 0|3|-1",
        type: "integer",
        position: 1,
        default: -1,
      },
      {
        name: "units",
        description: "(ADD/SUB) Units to use (ChronoUnit)",
        type: "enum",
        enum: ["NANOS", "MICROS", "MILLIS", "SECONDS", "MINUTES", "HOURS", "HALF_DAYS", "DAYS", "MONTHS", "YEARS"],
        position: 1,
        required: true,
      },
      {
        name: "resolution",
        description: "(EPOCH) Resolution of epoch (Seconds or Milliseconds)",
        type: "enum",
        enum: ["UNIX", "MS"],
        position: 1,
        default: "UNIX",
      },
    ],
  },
  decimal: {
    description: "Converts number to BigDecimal type",
    outputSchema: { $comment: "BigDecimal", type: "number" },
    arguments: [
      {
        name: "scale",
        description: "Scale of BigDecimal to set (default is 10 max)",
        type: "integer",
        position: 0,
        default: -1,
      },
      {
        name: "rounding",
        description: "Java\u0027s `RoundingMode` (default is HALF_UP)",
        type: "enum",
        enum: ["UP", "DOWN", "CEILING", "FLOOR", "HALF_UP", "HALF_DOWN", "HALF_EVEN"],
        position: 1,
        default: "HALF_UP",
      },
    ],
  },
  digest: {
    argBased: args => {
      if (typeof args?.algorithm === "string") {
        return DigestFunctionArgBasedSchemas[args.algorithm.toUpperCase()] ?? DigestFunctionArgBasedSchemas[""];
      }
      return DigestFunctionArgBasedSchemas[""];
    },
    description: "Creates a message digest based on a supported algorithm",
    inputSchema: { type: "string" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "format",
        description:
          'Format of output (BASE64 \u003d "The Base64 Alphabet" from RFC-2045, BAS64URL \u003d "URL and Filename safe Base64 Alphabet" from RFC-4648, HEX \u003d Hexadecimal string)',
        type: "enum",
        enum: ["BASE64", "BASE64URL", "HEX"],
        position: 1,
        default: "BASE64",
      },
      {
        name: "algorithm",
        description: "Hashing algorithm",
        type: "enum",
        enum: ["SHA-1", "SHA-256", "SHA-384", "SHA-512", "MD5", "JAVA"],
        position: 0,
        default: "SHA-1",
      },
    ],
  },
  distinct: {
    description:
      "Returns a distinct array (repeating elements removed, only primitive values are supported if no `by` was specified)",
    inputSchema: { type: "array" },
    arguments: [
      {
        name: "by",
        description:
          "A mapping for each element to distinct by (instead of the whole element, using ##current to refer to the current item)",
        type: "transformer",
        position: 0,
      },
    ],
    pipedType: true,
  },
  entries: {
    description: "Gets the entries* of an object or an array",
    notes: "* Entry is in the form of [ key / index, value ]",
    outputSchema: {
      type: "array",
      items: { type: "array", items: [{ type: "string" }, { type: undefined } as any] },
    },
  },
  eval: { description: "Evaluates the input and then transforms the context with the expression" },
  filter: {
    description: "Filter input array to all the elements that satisfy the predicate transformer",
    inputSchema: { type: "array" },
    arguments: [
      {
        name: "by",
        description:
          "A predicate transformer for an element (##current to refer to the current item and ##index to its index)",
        type: "transformer",
        position: 0,
      },
    ],
    pipedType: true,
  },
  find: {
    description: "Find the first element in a specified array that satisfy the predicate transformer",
    inputSchema: { type: "array" },
    arguments: [
      {
        name: "by",
        description:
          "A predicate transformer for an element (##current to refer to the current item and ##index to its index)",
        type: "transformer",
        position: 0,
        required: true,
      },
    ],
  },
  first: {
    description: "Returns the first non-null value",
    inputSchema: { type: "array" },
    aliasTo: EmbeddedTransformerFunction.coalesce,
  },
  flat: {
    description:
      "Flatten an array of arrays (non array elements will remain, all `null` elements are removed from result)",
    inputSchema: { type: "array" },
  },
  flatten: {
    description: "Flattens a JsonObject into a flat dot seperated list of entries",
    inputSchema: { type: "object" },
    outputSchema: { type: "object" },
    arguments: [
      {
        name: "array_prefix",
        description: "Sets how array elements should be prefixed, leave null to not flatten arrays",
        type: "string",
        position: 2,
        default: "$",
      },
      { name: "prefix", description: "A prefix to add to the base", type: "string", position: 1 },
      { name: "target", description: "A target to merge into", type: "object", position: 0 },
    ],
  },
  form: {
    description: "Converts an object to Form URL-Encoded string (a.k.a Query String)",
    notes:
      "Array values will be treated as multiple values for the same key (so the key will be duplicated in the result for each of the values)",
    inputSchema: { type: "object" },
    outputSchema: { type: "string" },
  },
  formparse: {
    description: "Parses a Form URL-Encoded string to `object`",
    notes:
      "Every element will have 2 forms in the result object:\nsingular with its original query name (e.g. q)\nplural with its name suffixed with $$ (e.g. q$$)",
    inputSchema: { type: "string" },
    outputSchema: { type: "object" },
  },
  group: {
    description:
      "Groups an array of entries into a map of key/arr[] by a specified transformer (with optional nested grouping)",
    notes: "Sorts elements of an array",
    inputSchema: { type: "array" },
    outputSchema: { type: "object" }, // TODO: each value schema of keys could be derived
    arguments: [
      {
        name: "by",
        description: "A transformer to extract a property to group by (using ##current to refer to the current item)",
        type: "transformer",
        position: 0,
        required: true,
      },
      {
        name: "then",
        description:
          'An array of subsequent grouping. When previous sort had no difference (only when `by` specified)\n{ "by": ..., "order": ..., "type": ...} // same 3 fields as above (`by` is required)',
        type: "array",
        position: 3,
      },
      {
        name: "type",
        description: "Type of values to expect when ordering the input array",
        type: "enum",
        enum: ["AUTO", "STRING", "NUMBER", "BOOLEAN"],
        position: 2,
        default: "AUTO",
      },
      {
        name: "order",
        description: "Direction of ordering (ascending / descending)",
        type: "enum",
        enum: ["ASC", "DESC"],
        position: 1,
        default: "ASC",
      },
    ],
  },
  if: {
    description:
      "Conditionally returns a value, if the evaluation of the condition argument is truthy (using the [Truthy logic]). A fallback value (if condition evaluates to false) is optional",
    inputSchema: {
      type: "any",
      description:
        "Either a value to evaluate for condition, or an Array of size 2 / 3 (with `condition`, `then` and optionally `else`)",
    },
    arguments: [
      { name: "else", description: "Value to return if condition is false", type: "any", position: 1 },
      { name: "then", description: "Value to return if condition is true", type: "any", position: 0 },
    ],
  },
  is: {
    description: "Checks value for one or more predicates (all predicates must be satisfied)",
    notes: "Inline form supports only `that`+`op` arguments. `gt`/`gte`/`lt`/`lte` - Uses the [Comparison logic]",
    outputSchema: { type: "boolean" },
    arguments: [
      {
        name: "op",
        description: "A type of check to do exclusively (goes together with `that`)",
        type: "enum",
        enum: [
          "IN",
          "NIN",
          "EQ",
          "\u003d",
          "\u003d\u003d",
          "NEQ",
          "!\u003d",
          "\u003c\u003e",
          "GT",
          "\u003e",
          "GTE",
          "\u003e\u003d",
          "LT",
          "\u003c",
          "LTE",
          "\u003c\u003d",
        ],
        position: 0,
      },
      { name: "that", description: "", type: "any", position: 1 },
      { name: "nin", description: "Array of values the input should **NOT** be part of", type: "array" },
      { name: "in", description: "Array of values the input should be part of", type: "array" },
      { name: "lt", description: "A Value the input should be lower than (input \u003c value)", type: "any" },
      {
        name: "gte",
        description: "A Value the input should be greater than or equal (input \u003e\u003d value)",
        type: "any",
      },
      { name: "neq", description: "A Value the input should **NOT** be equal to", type: "any" },
      { name: "eq", description: "A Value the input should be equal to", type: "any" },
      {
        name: "lte",
        description: "A Value the input should be lower than or equal (input \u003c\u003d value)",
        type: "any",
      },
      { name: "gt", description: "A Value the input should be greater than (input \u003e value)", type: "any" },
    ],
  },
  isnull: { description: "Returns true if value does not exist or equal to null", outputSchema: { type: "boolean" } },
  join: {
    description:
      'Joins an array of input as strings with an optional delimiter (default is `""`), prefix and suffix. `null` values are omitted',
    inputSchema: { type: "array" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "delimiter",
        description: "Delimiter to join any 2 adjacent elements",
        type: "string",
        position: 0,
        default: "",
      },
      { name: "prefix", description: "A string to prefix the result", type: "string", position: 1, default: "" },
      {
        name: "keep_nulls",
        description: "Whether to keep null values when joining",
        type: "boolean",
        position: 3,
        default: false,
      },
      { name: "suffix", description: "A string to suffix the result", type: "string", position: 2, default: "" },
    ],
  },
  json: {
    description: "Parses input as JSON string",
    inputSchema: { type: "string" },
    outputSchema: { type: "object" },
    aliasTo: EmbeddedTransformerFunction.jsonparse,
    deprecated: "jsonparse",
  },
  jsonparse: {
    description: "Parses input as JSON string",
    inputSchema: { type: "string" },
    outputSchema: { type: "object" },
  },
  jsonpatch: {
    description: "Apply patches defined by JSON Patch RFC-6902",
    outputSchema: { type: "object" },
    arguments: [
      {
        name: "ops",
        description: "A list of operations",
        type: "array",
        position: 0,
        required: true,
      },
    ],
  },
  jsonpath: {
    description: "Query a JSON document using JSONPath",
    outputSchema: { type: "object" },
    arguments: [
      {
        name: "path",
        description: "JSONPath expression",
        type: "string",
        position: 0,
        required: true,
      },
      {
        name: "options",
        description:
          "A list of options [by jayway](https://github.com/json-path/JsonPath?tab=readme-ov-file#tweaking-configuration)",
        type: "arrayofstring",
        position: 1,
      },
    ],
  },
  jsonpointer: {
    description: "Apply mutations on object paths using JSON Pointer (defined by RFC-6901)",
    outputSchema: { type: "object" },
    arguments: [
      {
        name: "op",
        description: "Operation",
        type: "enum",
        enum: ["GET", "SET", "REMOVE"],
        position: 0,
        default: "",
      },
      {
        name: "pointer",
        description: "JSON Pointer to apply operation on",
        type: "string",
        position: 1,
      },
      {
        name: "value",
        description: "Value to use",
        type: "any",
        position: 2,
      },
    ],
  },
  jwtparse: {
    description: "Parses JWT tokens and returns their payload",
    notes: "This function does not validate the token. Only returns its payload (claims)",
    inputSchema: { type: "string" },
    outputSchema: { type: "object" },
  },
  length: {
    description: "Returns the length of a value",
    outputSchema: {
      type: "integer",
    },
    arguments: [
      {
        name: "default_zero",
        description: "Whether to return 0 instead of null (on any kind of issue)",
        type: "boolean",
        position: 1,
        default: false,
      },
      {
        name: "type",
        description:
          "Restrict the type of value to check length of (if specified type no detected the result will be null)",
        type: "enum",
        enum: ["AUTO", "STRING", "ARRAY", "OBJECT"],
        position: 0,
        default: "AUTO",
      },
    ],
  },
  long: {
    description: "Converts to integer, all decimal digits are truncated",
    outputSchema: { $comment: "Long", type: "integer" },
  },
  lookup: {
    description: "Joins multiple arrays of objects by a dynamic condition (transformer) on each pair of matches",
    notes:
      '```\n{\n  "$$lookup": {items1},\n  "using": [\n    { "with": {items2}, "as": "name", "on": {callback transformer (#current, #index, #{as})} }\n    { "with": {items2}, "as": "name", "on": {callback transformer (#current, #index, #{as})} }\n    ...\n  ],\n  "to": {callback transformer} // optional - default is (item1 \u003c- item2 \u003c- item3 ...; appends and overwrites)\n}\n```',
    inputSchema: { type: "array" },
    arguments: [
      {
        name: "using",
        description: "Array of definitions of how to match other arrays to the main one",
        type: "array",
        position: 0,
        required: true,
      },
      {
        name: "to",
        description: "Transformer to map each pair of elements to its value in the result array",
        type: "transformer",
        position: 1,
      },
    ],
  },
  lower: {
    description: "Converts a string to all lowercase letters",
    inputSchema: { type: "string" },
    outputSchema: { type: "string" },
  },
  map: {
    description: "Returns a mapped array applying the transformer on each of the elements",
    inputSchema: { type: "array" },
    arguments: [
      {
        name: "to",
        description: "Transformer to map each element to its value in the result array (inputs: ##current, ##index)",
        type: "transformer",
        position: 0,
      },
    ],
  },
  match: {
    description: "Returns a matched substring from input by a pattern (and optionally group id)",
    inputSchema: { type: "object" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "pattern",
        description: "Regular expression to match and extract from input string",
        type: "string",
        position: 0,
        required: true,
      },
      { name: "group", description: "The group id to get", type: "integer", position: 1, default: 0 },
    ],
  },
  matchall: {
    description: "Returns all matches substring from input by a pattern (and optionally group id)",
    inputSchema: { type: "object" },
    outputSchema: { type: "array", items: { type: "string" } },
    arguments: [
      {
        name: "pattern",
        description: "Regular expression to match and extract from input string",
        type: "string",
        position: 0,
        required: true,
      },
      { name: "group", description: "The group id to get", type: "integer", position: 1, default: 0 },
    ],
  },
  math: {
    description: "Evaluate a mathematical expression",
    inputSchema: {
      type: "number",
      $comment: "BigDecimal",
      description: "op1 / op / [op1, op, op2] / [op, op1, op2]",
    },
    outputSchema: { $comment: "BigDecimal", type: "number" },
    arguments: [
      {
        name: "op2",
        description: "Second operand or scale for ROUND/FLOOR/CEIL",
        type: "bigdecimal",
        position: 2,
        default: 0.0,
      },
      { name: "op1", description: "First operand", type: "bigdecimal", position: 0, required: true },
      {
        name: "op",
        description: "",
        type: "any",
        enum: [
          "+",
          "-",
          "*",
          "/",
          "//",
          "%",
          "^",
          "&",
          "|",
          "~",
          "<<",
          ">>",
          "MIN",
          "MAX",
          "SQRT",
          "ROUND",
          "FLOOR",
          "CEIL",
          "ABS",
          "NEG",
          "SIG",
        ],
        position: 1,
        required: true,
      },
    ],
  },
  max: {
    description: "Returns the max of all values in the array",
    inputSchema: { type: "array" },
    outputSchemaTemplate: { type: "object" },
    arguments: [
      { name: "default", description: "The default value to use for empty values", type: "object", position: 0 },
      {
        name: "by",
        description: "A transformer to extract a property to sum by (using ##current to refer to the current item)",
        type: "transformer",
        position: 2,
      },
      {
        name: "type",
        description: "Type of values to expect when ordering the input array",
        type: "enum",
        enum: ["AUTO", "STRING", "NUMBER", "BOOLEAN"],
        position: 1,
        default: "AUTO",
      },
    ],
  },
  min: {
    description: "Returns the min of all values in the array",
    inputSchema: { type: "array" },
    outputSchemaTemplate: { type: "object" },
    arguments: [
      { name: "default", description: "The default value to use for empty values", type: "object", position: 0 },
      {
        name: "by",
        description: "A transformer to extract a property to sum by (using ##current to refer to the current item)",
        type: "transformer",
        position: 2,
      },
      {
        name: "type",
        description: "Type of values to expect when ordering the input array",
        type: "enum",
        enum: ["AUTO", "STRING", "NUMBER", "BOOLEAN"],
        position: 1,
        default: "AUTO",
      },
    ],
  },
  normalize: {
    description: "Replace special characters forms with their simple form equivalent (removing marks by default)",
    notes:
      '- Allows post-processing over Java\u0027s normalizer algorithm result\n### Post Operations\n- `ROBUST` - Try to return the most of similar letters to latin, replaced to their latin equivalent, including:\n  - Removing combining diacritical marks (works with NFD/NFKD which leaves the characters decomposed)\n  - Stroked (and others which are not composed) (i.e. "ĐŁłŒ" -\u003e "DLlOE")\n  - Replacing (with space) and trimming white-spaces\n',
    inputSchema: { type: "string" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "form",
        description:
          "Normalizer Form (as described in Java\u0027s documentation. Default is NFKD; Decompose for compatibility)",
        type: "enum",
        enum: ["NFD", "NFC", "NFKD", "NFKC"],
        position: 0,
        default: "NFKD",
      },
      {
        name: "post_operation",
        description: "Post operation to run on result to remove/replace more letters",
        type: "enum",
        enum: ["ROBUST", "NONE"],
        position: 1,
        default: "ROBUST",
      },
    ],
  },
  not: {
    description: "Returns the opposite of the argument\u0027s boolean value (this returns the opposite of `$$boolean`)",
    inputSchema: { type: "object" },
    outputSchema: { type: "boolean" },
    arguments: [
      {
        name: "style",
        description:
          "Style of considering truthy values (JAVA/JS) (JS only relates to string handling; not objects and arrays)",
        type: "enum",
        position: 0,
        default: "JAVA",
      },
    ],
  },
  numberformat: {
    description: "Formats a number",
    inputSchema: { $comment: "BigDecimal", type: "number" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "radix",
        description: "(BASE) Radix to be used for formatting input",
        type: "integer",
        position: 1,
        default: 10,
      },
      {
        name: "compact_style",
        description: "(COMPACT) Type of compacting format",
        type: "enum",
        enum: ["SHORT", "LONG"],
        position: 2,
        default: "SHORT",
      },
      {
        name: "pattern",
        description: "(DECIMAL) See [tutorial](https://docs.oracle.com/javase/tutorial/i18n/format/decimalFormat.html)",
        type: "string",
        position: 2,
        default: "#0.00",
      },
      {
        name: "type",
        description: "Type of output format",
        type: "enum",
        enum: ["NUMBER", "DECIMAL", "CURRENCY", "PERCENT", "INTEGER", "COMPACT", "BASE"],
        position: 0,
        default: "NUMBER",
      },
      {
        name: "locale",
        description: "Locale to use (language and country specific formatting; set by Java, default is en-US)",
        type: "string",
        position: 1,
      },
      {
        name: "decimal",
        description: "(DECIMAL) A custom character to be used for decimal point (default is .)",
        type: "string",
        position: 4,
      },
      {
        name: "grouping",
        description: "(DECIMAL) A custom character to be used for grouping (default is ,)",
        type: "string",
        position: 3,
      },
      {
        name: "currency",
        description:
          "(CURRENCY) Currency to use in format ([ISO-4217 currency code](https://www.iso.org/iso-4217-currency-codes.html))",
        type: "string",
        position: 2,
      },
    ],
  },
  numberparse: {
    description: "Parses a number from string",
    inputSchema: { type: "string" },
    outputSchema: { $comment: "BigDecimal", type: "number" },
    arguments: [
      {
        name: "radix",
        description: "(BASE) Radix to be used in interpreting input",
        type: "integer",
        position: 1,
        default: 10,
      },
      {
        name: "pattern",
        description: "See [tutorial](https://docs.oracle.com/javase/tutorial/i18n/format/decimalFormat.html)",
        type: "string",
        position: 0,
        default: "#0.00",
      },
      {
        name: "locale",
        description: "Locale to use (language and country specific formatting; set by Java, default is en-US)",
        type: "string",
        position: 1,
      },
      {
        name: "decimal",
        description: "A custom character to be used for decimal point (default is .)",
        type: "string",
        position: 3,
      },
      {
        name: "grouping",
        description: "A custom character to be used for grouping (default is ,)",
        type: "string",
        position: 2,
      },
    ],
  },
  object: {
    description: "Reduces an array of entries into an object",
    notes: "- Entry is in the form of [ key, value ]",
    inputSchema: { type: "array" },
    outputSchema: { type: "object" },
  },
  or: {
    description: "Evaluates to `true` if any of the values provided will evaluate to `true` (using the [Truthy logic])",
    inputSchema: { type: "array" },
    outputSchema: { type: "boolean" },
  },
  pad: {
    description: "Pad a provided string with a certain character repeated until a certain width of output string",
    notes: "(Strings longer than `width` will be returned as-is)",
    outputSchema: { type: "string" },
    arguments: [
      { name: "pad_string", description: "The character(s) to pad with", type: "string", position: 2, default: "0" },
      {
        name: "width",
        description: "What is the maximum length of the output string",
        type: "integer",
        position: 1,
        required: true,
      },
      {
        name: "direction",
        description: "On which side of the input to pad",
        type: "enum",
        enum: ["LEFT", "START", "RIGHT", "END"],
        position: 0,
        required: true,
      },
    ],
  },
  partition: {
    description: "Partition an array to multiple constant size arrays",
    inputSchema: { type: "array" },
    outputSchemaTemplate: { items: { type: "array" }, type: "array", description: "of same items type as input" },
    arguments: [
      { name: "size", description: "The size of each partition", type: "integer", position: 0, required: true },
    ],
  },
  range: {
    description: "Creates an array with a sequence of numbers starting with `start` up-to `end` in steps of `step`",
    outputSchema: { items: { $comment: "BigDecimal", type: "number" }, type: "array" },
    arguments: [
      { name: "start", description: "First value", type: "bigdecimal", position: 0, required: true },
      {
        name: "end",
        description: "Max value to appear in sequence",
        type: "bigdecimal",
        position: 1,
        required: true,
      },
      {
        name: "step",
        description: "Step to add on each iteration to the previous value in the sequence",
        type: "bigdecimal",
        position: 2,
        default: 1.0,
      },
    ],
  },
  raw: { description: "Returns the input as-is without interpreting transformers" },
  reduce: {
    description: "Reduce an array with an initial value (`identity`) and a context transformer to a single value",
    inputSchema: { type: "array" },
    arguments: [
      { name: "identity", description: "Initial value to start the accumulation with", type: "any", position: 1 },
      {
        name: "to",
        description:
          "Transformer to apply on each element (with last accumulation) to get the next accumulation (inputs: ##accumulator, ##current, ##index)",
        type: "transformer",
        position: 0,
        required: true,
      },
    ],
  },
  replace: {
    description: "Search and replaces a substring in the given input",
    inputSchema: { type: "string" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "find",
        description:
          "Value to search in input string (depends on `type`, if set to `REGEX`, should be a regular expression)",
        type: "string",
        position: 0,
        required: true,
      },
      {
        name: "from",
        description: "At what index in the string the search should start from",
        type: "integer",
        position: 3,
        default: 0,
      },
      {
        name: "type",
        description: "Matching type",
        type: "enum",
        enum: ["STRING", "FIRST", "REGEX", "REGEX-FIRST"],
        position: 2,
        default: "STRING",
      },
      {
        name: "replacement",
        description:
          "Value to replace each match (or only first if `type \u003d REGEX-FIRST`), when using regular expression can use group matches (e.g. `$1`) (Note: to escape `$` if starting with it)",
        type: "string",
        position: 1,
        default: "",
      },
    ],
  },
  reverse: {
    description: "Reverses the order of elements in an array",
    pipedType: true,
    inputSchema: { type: "array" },
  },
  slice: {
    description: "Gets a slice of an array by indices (negative begin index will slice from the end)",
    inputSchema: {
      type: "array",
      description: "Array to fetch from",
    },
    arguments: [
      {
        name: "end",
        description: "Index of last element to slice to (if negative, counts from the end of the array)",
        type: "integer",
        position: 1,
      },
      {
        name: "begin",
        description: "Index of element to start slice from (if negative, counts from the end of the array)",
        type: "integer",
        position: 0,
        required: true,
      },
    ],
  },
  sort: {
    description: "Sorts elements of an array",
    inputSchema: { type: "array" },
    arguments: [
      {
        name: "by",
        description: "A transformer to extract a property to sort by (using ##current to refer to the current item)",
        type: "transformer",
        position: 0,
        required: true,
      },
      {
        name: "then",
        description:
          'An array of secondary sorting in case previous sorting returned equal, first being the root fields (Every item require the `by` field while other fields are optional)\n{ "by": ..., "order": ..., "type": ...} // same 3 fields as above (`by` is required)',
        type: "array",
        position: 3,
      },
      {
        name: "type",
        description: "Type of values to expect when ordering the input array",
        type: "enum",
        enum: ["AUTO", "STRING", "NUMBER", "BOOLEAN"],
        position: 2,
        default: "AUTO",
      },
      {
        name: "order",
        description: "Direction of ordering (ascending / descending)",
        type: "enum",
        enum: ["ASC", "DESC"],
        position: 1,
        default: "ASC",
      },
    ],
    pipedType: true,
  },
  split: {
    description: "Splits a string using a given delimiter/regex",
    inputSchema: { type: "string" },
    outputSchema: { items: { type: "string" }, type: "array" },
    arguments: [
      {
        name: "delimiter",
        description: "Delimiter to split by (can be a regular expression)",
        type: "string",
        position: 0,
        default: "",
      },
      {
        name: "limit",
        description: "Limit the amount of elements returned (and by that, the amount the pattern get matched)",
        type: "integer",
        position: 1,
        default: 0,
      },
    ],
  },
  string: {
    description: "Converts to string (if `json` set to `true`, will convert null and strings also as JSON strings)",
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "json",
        description:
          "Whether to convert `null` and strings to json (otherwise, null stays null and strings are returned as-is)",
        type: "boolean",
        position: 0,
        default: false,
      },
    ],
  },
  substring: {
    description: "Gets a slice of a string by indices (negative beginIndex will slice from the end)",
    inputSchema: { type: "string" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "end",
        description: "Index of last character to slice to (if negative, counts from the end of the string)",
        type: "integer",
        position: 1,
      },
      {
        name: "begin",
        description: "Index of first character to slice from (if negative, counts from the end of the string)",
        type: "integer",
        position: 0,
        required: true,
      },
    ],
  },
  sum: {
    description: "Returns a sum of all values in the array",
    inputSchema: { type: "array" },
    outputSchema: { $comment: "BigDecimal", type: "number" },
    arguments: [
      {
        name: "default",
        description: "The default value to use for empty values",
        type: "bigdecimal",
        position: 0,
        default: 0.0,
      },
      {
        name: "by",
        description: "A transformer to extract a property to sum by (using ##current to refer to the current item)",
        type: "transformer",
        position: 1,
      },
    ],
  },
  switch: {
    description:
      "Switch-case expression. Value is compared to each of the keys in cases and a matching **key** will result with its **value**, otherwise `default` value or `null` will be returned.",
    inputSchema: { type: "any", description: "Value to test" },
    arguments: [
      {
        name: "default",
        description: "Fallback value in case no match to any key in cases",
        type: "any",
        position: 1,
      },
      { name: "cases", description: "A map of cases (string to value)", type: "object", position: 0, required: true },
    ],
  },
  test: {
    description: "Checks if a string matches a certain pattern",
    inputSchema: { type: "string" },
    outputSchema: { type: "boolean" },
    arguments: [
      {
        name: "pattern",
        description: "Regular expression to match against input string",
        type: "string",
        position: 0,
        required: true,
      },
    ],
  },
  transform: {
    description: "Applies a transformation inside a transformer (Useful for piping functions results)",
    arguments: [
      {
        name: "to",
        description: "Transformer to apply on input (inputs: ##current)",
        type: "transformer",
        position: 0,
      },
    ],
  },
  trim: {
    description: "Removes whitespaces from sides of string",
    inputSchema: { type: "string" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "type",
        description: "Type of trimming",
        type: "enum",
        enum: ["BOTH", "START", "END", "INDENT", "JAVA"],
        position: 0,
        default: "BOTH",
      },
    ],
  },
  unflatten: {
    description: "Accepts an objet with dot separated field names and merges them into an hierarchical object",
    inputSchema: { type: "object" },
    outputSchema: { type: "object" },
    arguments: [
      { name: "path", description: "The root path in the target to merge into", type: "string", position: 1 },
      { name: "target", description: "A target to merge into", type: "object", position: 0 },
    ],
  },
  upper: {
    description: "Converts a string to all uppercase letters",
    inputSchema: { type: "string" },
    outputSchema: { type: "string" },
  },
  uriparse: {
    description: "Parses a URI to its components",
    inputSchema: {
      type: "string",
    },
    outputSchema: {
      type: "object",
      properties: {
        scheme: {
          type: "string",
        },
        user_info: {
          type: "string",
        },
        authority: {
          type: "string",
        },
        host: {
          type: "string",
        },
        hostname: {
          type: "string",
        },
        port: {
          type: "integer",
        },
        path: {
          type: "string",
        },
        query: {
          type: "object",
        },
        query_raw: {
          type: "string",
        },
        fragment: {
          type: "string",
        },
      },
    },
  },
  urldecode: {
    description: "URL decodes as string",
    inputSchema: { type: "string" },
    outputSchema: { type: "string" },
  },
  urlencode: {
    description: "URL encodes as string",
    inputSchema: { type: "string" },
    outputSchema: { type: "string" },
  },
  uuid: {
    description: "Format and transform UUID",
    notes:
      '- `NO_HYPHENS` (can also be specified as `N`) - Same as `CANONICAL` with hyphens removed\n- `BASE36` (can also be specified as `B36`) - alphanumeric alphabet\n- `BASE62` (can also be specified as `B62`) - alphanumeric alphabet, case sensitive\n- `BASE64` (can also be specified as `B64`) - "URL and Filename safe Base64 Alphabet"\n- `V3` - Consider input as name and generate a UUIDv3 (name-based, RFC 4122) (namespace optionally used)\n- `V5` - Consider input as name and generate a UUIDv5 (name-based, RFC 4122) (namespace optionally used)\n',
    inputSchema: {
      type: "string",
      description:
        "Input must be a UUID in standard string format (RFC 4122; with hyphens), can be used in conjunction with `#uuid`",
    },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "format",
        description: "Formatting (or generation in case of v3/v5)",
        type: "enum",
        enum: ["CANONICAL", "NO_HYPHENS", "BASE62", "BASE64", "BASE36", "V3", "V5"],
        position: 0,
        default: "CANONICAL",
      },
      { name: "namespace", description: "UUID to be used as salt (for V3/V5)", type: "string", position: 1 },
    ],
  },
  value: { description: "Returns the value if it passes the truthy test, or null otherwise" },
  wrap: {
    description: "Wrap an input string with `prefix` and `suffix`",
    inputSchema: { type: "string" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "prefix",
        description: "String that will prefix the input in the output",
        type: "string",
        position: 0,
        default: "",
      },
      {
        name: "suffix",
        description: "String that will suffix the input in the output",
        type: "string",
        position: 1,
        default: "",
      },
    ],
  },
  xml: {
    description:
      "Converts an object to XML string (a wrapper element can be added by specifying the field `root` with the element name)",
    inputSchema: { type: "object" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "root",
        description: "Name for a wrapper element (e.g. an array was passed and a container is needed)",
        type: "string",
        position: 0,
      },
      { name: "xslt", description: "XSLT document to transform xml created from input", type: "string", position: 1 },
    ],
  },
  xmlparse: {
    description: "Parses XML String to Object (powered by `org.json.XML`)",
    inputSchema: { type: "string" },
    outputSchema: { type: "object" },
    arguments: [
      {
        name: "cdata_tag_name",
        description: "A key for the CDATA section",
        type: "string",
        position: 1,
        default: "$content",
      },
      {
        name: "keep_strings",
        description: "Do not try to detect primitive types (e.g. numbers, boolean, etc)",
        type: "boolean",
        position: 0,
        default: false,
      },
      {
        name: "force_list",
        description: "Tag names that will always be parsed as arrays",
        type: "arrayofstring",
        position: 3,
      },
      {
        name: "convert_nil_to_null",
        description: 'If values with attribute `xsi:nil\u003d"true"` will be converted to `null`',
        type: "boolean",
        position: 2,
        default: false,
      },
    ],
  },
  xor: {
    description:
      "Evaluates to `true` if only one of the values provided will evaluate to `true` (using the [Truthy logic])",
    inputSchema: { type: "array" },
    outputSchema: { type: "boolean" },
  },
  yaml: {
    description: "Converts an object to YAML format",
    inputSchema: { type: "object" },
    outputSchema: { type: "string" },
  },
  yamlparse: {
    description: "Parses a YAML format to `object`",
    inputSchema: { type: "string" },
    outputSchema: { type: "object" },
  },
});
