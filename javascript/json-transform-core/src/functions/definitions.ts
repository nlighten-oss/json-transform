import { EmbeddedTransformerFunction, FunctionDefinition } from "./types";

export default {
  and: {
    description:
      "Evaluates to `true` if all values provided will evaluate to `true` (using the [Truthy logic](../truthy-logic))",
    inputSchema: { type: "array", required: true, description: "Values to check" },
    outputSchema: { type: "boolean" },
  },
  at: {
    description: "Retrieves an element from a specific position inside an input array",
    inputSchema: { type: "array", required: true, description: "Array to fetch from" },
    outputSchemaTemplate: { type: "object", description: "Same as value at specified index" },
    arguments: [
      {
        name: "index",
        description:
          "The index of element to return, **negative** indices will return element from the end (`-n -> length - n`)",
        type: "integer",
        position: 0,
        required: true,
      },
    ],
  },
  avg: {
    description: "Returns the average of all values in the array",
    inputSchema: { type: "array", required: true, description: "Array to average" },
    outputSchema: { type: "number", $comment: "BigDecimal" },
    arguments: [
      {
        name: "default",
        description: "The default value to use for empty values",
        type: "BigDecimal",
        position: 0,
        default: 0.0,
      },
      {
        name: "by",
        description: "A transformer to extract a property to sum by (using `##current` to refer to the current item)",
        type: "transformer",
        position: 1,
        default: "##current",
        transformerArguments: [{ name: "##current", type: "any", position: 0, description: "Current element" }],
      },
    ],
  },
  base64: {
    description: "Encode to or decode from base64",
    inputSchema: { type: "string", required: true, description: "Value to encode/decode" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "action",
        description: "Whether to encode or decode input",
        type: "enum",
        enum: ["ENCODE", "DECODE"],
        position: 0,
        default: "ENCODE",
      },
      {
        name: "rfc",
        description:
          'Which alphabet to use (`BASIC` = "The Base64 Alphabet" from RFC-2045, `URL` = "URL and Filename safe Base64 Alphabet" from RFC-4648, `MIME` = Same as `BASIC` but in lines with no more than 76 characters each)',
        type: "enum",
        enum: ["BASIC", "URL", "MIME"],
        position: 1,
        default: "BASIC",
      },
      {
        name: "without_padding",
        description: "Don't add padding at the end of the output (The character `=`)",
        type: "boolean",
        position: 2,
        default: false,
      },
    ],
  },
  boolean: {
    description: "Evaluates input to boolean using the [Truthy logic](../truthy-logic)",
    usageNotes:
      'Strings evaluation depends on `style` argument:\n- By default, value must be `"true"` for `true`.\n- Unless `style` is set to `JS`, then any non-empty value is `true`. Arrays and objects of size 0 returns `false`.\n',
    inputSchema: { type: "any" },
    outputSchema: { type: "boolean" },
    arguments: [
      {
        name: "style",
        description:
          "Style of considering truthy values (`JS` only relates to string handling; not objects and arrays)",
        type: "enum",
        enum: ["JAVA", "JS"],
        position: 0,
        default: "JAVA",
      },
    ],
  },
  coalesce: {
    aliases: ["first"],
    description: "Returns the first non-null value",
    notes: ":::tip\nCoalesce can also be referred to as `$$first` instead of `$$coealesce`\n:::\n",
    inputSchema: { type: "array", required: true, description: "Array of elements (may include nulls)" },
    outputSchemaTemplate: { type: "object", description: "Same as first non-null value" },
  },
  concat: {
    description: "Concatenates primary value array with elements or other arrays of elements",
    notes: ":::note\nElements which are `null` on the primary value will be ignored.\n:::",
    inputSchema: {
      type: "array",
      required: true,
      description: "Array of arrays / elements (null elements are ignored)",
    },
    outputSchemaTemplate: { type: "array" },
  },
  contains: {
    description: "Checks whether an array contains a certain value",
    inputSchema: {
      type: "array",
      required: true,
      description: "Array of arrays / elements (null elements are ignored)",
    },
    outputSchema: { type: "boolean" },
    arguments: [{ name: "that", required: true, description: "The value to look for", type: "any", position: 0 }],
  },
  csv: {
    description: "Converts an array of objects/arrays to a CSV string",
    inputSchema: { type: "array", required: true, description: "Array of objects/arrays" },
    outputSchemaTemplate: { type: "string", description: "CSV" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "no_headers",
        description: "Whether to include object keys as headers (taken from first object if no `names`)",
        type: "boolean",
        position: 0,
        default: false,
      },
      {
        name: "force_quote",
        description: "Whether to quote all the values",
        type: "boolean",
        position: 1,
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
        name: "names",
        description:
          "Names of fields to extract into csv if objects (will be used as the header row, unless `no_headers`)",
        type: "string[]",
        position: 3,
      },
    ],
  },
  csvparse: {
    description: "Converts a CSV string into an array of objects/arrays",
    inputSchema: { type: "string", required: true, description: "csv contents" },
    outputSchema: { type: "array", items: { type: "object" } },
    subfunctions: [
      {
        if: [{ argument: "no_headers", equals: "TRUE" }],
        then: {
          outputSchema: { type: "array", items: { type: "array" } },
          description: "Converts a CSV string into an array of arrays",
          arguments: [
            {
              name: "no_headers",
              description: "Whether to treat the first row as object keys",
              type: "const",
              position: 0,
              const: true,
            },
            {
              name: "separator",
              description: "Use an alternative field separator",
              type: "string",
              position: 1,
              default: ",",
            },
            {
              name: "names",
              description:
                "Names of fields of input arrays (by indices) or objects (can sift if provided less names than there are in the objects provided)",
              type: "array",
              position: 2,
            },
          ],
        },
      },
    ],
    arguments: [
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
      {
        name: "names",
        description:
          "Names of fields of input arrays (by indices) or objects (can sift if provided less names than there are in the objects provided)",
        type: "array",
        position: 2,
      },
    ],
  },
  date: {
    description: "Date manipulation and formatting utility",
    notes:
      ":::note\ninput must be a Date in ISO-8601 format or Date or Instant\n:::\n\n:::tip\nUseful to be used in conjunction with `#now` as input\n:::\n",
    inputSchema: { type: "string", required: true, description: "Date or Date-Time in ISO-8601 format" },
    outputSchema: { type: "string", format: "date-time" },
    subfunctions: [
      {
        if: [{ argument: "format", equals: "ISO" }],
        then: {
          outputSchema: { type: "string", format: "date-time" },
          description:
            "Returns the ISO-8601 representation of the input date (to the specified precision set by `digits`)",
          arguments: [
            {
              name: "format",
              type: "const",
              position: 0,
              required: true,
              const: "ISO",
            },
            {
              name: "digits",
              description: "precision for time part (scale) 0|3|6|9|-1 (-1 means maximum)",
              type: "integer",
              position: 1,
              default: -1,
            },
          ],
        },
      },
      {
        if: [{ argument: "format", equals: "GMT" }],
        then: {
          outputSchema: { type: "string" },
          description: "RFC-1123 format",
          arguments: [
            {
              name: "format",
              type: "const",
              position: 0,
              required: true,
              const: "GMT",
            },
          ],
        },
      },
      {
        if: [{ argument: "format", equals: "DATE" }],
        then: {
          outputSchema: { type: "string", format: "date" },
          description: "Date part of ISO 8601",
          arguments: [
            {
              name: "format",
              type: "const",
              position: 0,
              required: true,
              const: "DATE",
            },
          ],
        },
      },
      {
        if: [{ argument: "format", equals: "ADD" }],
        then: {
          outputSchema: { type: "string", format: "date-time" },
          description: "Adds an amount chronological units (`ChronoUnit`, see Java docs) to input date",
          arguments: [
            {
              name: "format",
              type: "const",
              position: 0,
              required: true,
              const: "ADD",
            },
            {
              name: "units",
              description: "Units to use (ChronoUnit)",
              type: "enum",
              enum: [
                "NANOS",
                "MICROS",
                "MILLIS",
                "SECONDS",
                "MINUTES",
                "HOURS",
                "HALF_DAYS",
                "DAYS",
                "MONTHS",
                "YEARS",
              ],
              position: 1,
              required: true,
            },
            {
              name: "amount",
              description: "Amount of units to add (can be negative)",
              type: "long",
              position: 2,
              default: 0,
            },
          ],
        },
      },
      {
        if: [{ argument: "format", equals: "SUB" }],
        then: {
          outputSchema: { type: "string", format: "date-time" },
          description: "Subtracts an amount chronological units (`ChronoUnit`, see Java docs) from input date",
          arguments: [
            {
              name: "format",
              type: "const",
              position: 0,
              required: true,
              const: "SUB",
            },
            {
              name: "units",
              description: "Units to use (ChronoUnit)",
              type: "enum",
              enum: [
                "NANOS",
                "MICROS",
                "MILLIS",
                "SECONDS",
                "MINUTES",
                "HOURS",
                "HALF_DAYS",
                "DAYS",
                "MONTHS",
                "YEARS",
              ],
              position: 1,
              required: true,
            },
            {
              name: "amount",
              description: "Amount of units to subtract (can be negative)",
              type: "long",
              position: 2,
              default: 0,
            },
          ],
        },
      },
      {
        if: [{ argument: "format", equals: "DIFF" }],
        then: {
          outputSchema: { type: "integer" },
          description: "Calculate the difference between two dates in specified units.",
          arguments: [
            {
              name: "format",
              type: "const",
              position: 0,
              required: true,
              const: "DIFF",
            },
            {
              name: "units",
              description: "Units to use (ChronoUnit)",
              type: "enum",
              enum: ["NANOS", "MICROS", "MILLIS", "SECONDS", "MINUTES", "HOURS", "HALF_DAYS", "DAYS"],
              position: 1,
              required: true,
            },
            {
              name: "end",
              description: "End date",
              type: "string",
              position: 2,
              required: true,
            },
          ],
        },
      },
      {
        if: [{ argument: "format", equals: "EPOCH" }],
        then: {
          outputSchema: { type: "integer" },
          description: "Seconds passed since 1970-01-01; unless `resolution`=`MS` then milliseconds,",
          arguments: [
            {
              name: "format",
              type: "const",
              position: 0,
              required: true,
              const: "EPOCH",
            },
            {
              name: "resolution",
              description: "Resolution of epoch (Seconds or Milliseconds)",
              type: "enum",
              enum: ["UNIX", "MS"],
              position: 1,
              default: "UNIX",
            },
          ],
        },
      },
      {
        if: [{ argument: "format", equals: "FORMAT" }],
        then: {
          outputSchema: { type: "string" },
          description: "Format using a date format pattern (Java style)",
          arguments: [
            {
              name: "format",
              type: "const",
              position: 0,
              required: true,
              const: "FORMAT",
            },
            { name: "pattern", description: "Pattern to use", type: "string", position: 1, required: true },
            { name: "timezone", description: "Time zone to use", type: "string", position: 2, default: "UTC" },
          ],
        },
      },
      {
        if: [{ argument: "format", equals: "ZONE" }],
        then: {
          outputSchema: { type: "string", format: "date-time" },
          description: "Returns the ISO-8601 with offset by specifying a timezone",
          arguments: [
            {
              name: "format",
              type: "const",
              position: 0,
              required: true,
              const: "ZONE",
            },
            {
              name: "zone",
              description: "Time zone to use\n- Java's `ZoneId` (with `SHORT_IDS`)",
              type: "enum",
              enum: [],
              position: 1,
              default: "UTC",
            },
          ],
        },
      },
    ],
    arguments: [
      {
        name: "format",
        description: "Formatter to use",
        type: "enum",
        enum: ["ISO", "GMT", "DATE", "ADD", "SUB", "DIFF", "EPOCH", "FORMAT", "ZONE"],
        position: 0,
        default: "ISO",
      },
    ],
  },
  decimal: {
    description: "Converts number to BigDecimal type",
    inputSchema: { type: "any", required: true, description: "Value to convert" },
    outputSchema: { type: "number", $comment: "BigDecimal" },
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
        description: "Java\u0027s `RoundingMode`",
        type: "enum",
        enum: ["UP", "DOWN", "CEILING", "FLOOR", "HALF_UP", "HALF_DOWN", "HALF_EVEN"],
        position: 1,
        default: "HALF_UP",
      },
    ],
  },
  digest: {
    description: "Creates a message digest based on a supported algorithm",
    inputSchema: { type: "string" },
    outputSchema: { type: "string" },
    subfunctions: [
      {
        if: [{ argument: "algorithm", equals: "JAVA" }],
        then: {
          outputSchema: { type: "integer" },
          description: "Creates a message digest based on Java's hashCode()",
          arguments: [
            {
              name: "algorithm",
              type: "const",
              position: 0,
              const: "JAVA",
            },
          ],
        },
      },
    ],
    arguments: [
      {
        name: "algorithm",
        description: "Hashing algorithm",
        type: "enum",
        enum: ["SHA-1", "SHA-256", "SHA-384", "SHA-512", "MD5", "JAVA"],
        position: 0,
        default: "SHA-1",
      },
      {
        name: "format",
        description:
          'Format of output (`BASE64` = "The Base64 Alphabet" from RFC-2045, `BAS64URL` = "URL and Filename safe Base64 Alphabet" from RFC-4648, `HEX` = Hexadecimal string)',
        type: "enum",
        enum: ["BASE64", "BASE64URL", "HEX"],
        position: 1,
        default: "BASE64",
      },
    ],
  },
  distinct: {
    description:
      "Returns a distinct array (repeating elements removed, only primitive values are supported if no `by` was specified)",
    inputSchema: { type: "array", required: true, description: "Array of elements" },
    outputSchemaTemplate: { type: "array", description: "Same items type as input" },
    pipedType: true,
    arguments: [
      {
        name: "by",
        description:
          "A mapping for each element to distinct by (instead of the whole element, using `##current` to refer to the current item)",
        type: "transformer",
        position: 0,
        default: "##current",
        transformerArguments: [{ name: "##current", type: "any", position: 0, description: "Current element" }],
      },
    ],
  },
  entries: {
    description: "Gets the entries* of an object or an array",
    notes: ":::note\n*Entry is in the form of `[ key / index, value ]`\n:::",
    inputSchema: {
      type: "any",
      required: true,
      description: "An `object` or an `array` to get entries from",
    },
    outputSchema: {
      type: "array",
      items: { type: "array", items: [{ type: "string" }, { type: undefined }] },
    },
  },
  eval: {
    description: "Evaluates the input and then transforms the context with the expression",
    inputSchema: { type: "any", required: true, description: "Transformer definition" },
  },
  every: {
    aliases: ["all"],
    description: "Checks if all elements in an array satisfy a predicate",
    usageNotes:
      ":::info\npredicate `by` should resolve to a `boolean` value, it uses the [truthy logic](../truthy-logic)\n:::",
    inputSchema: { type: "array", required: true, description: "Array of elements" },
    outputSchema: { type: "boolean" },
    arguments: [
      {
        name: "by",
        description: "A predicate transformer for an element",
        type: "transformer",
        position: 0,
        required: true,
        transformerArguments: [{ name: "##current", type: "any", position: 0, description: "Current element" }],
      },
    ],
  },
  filter: {
    description: "Filter input array to all the elements that satisfy the predicate transformer",
    usageNotes:
      ":::info\npredicate `by` should resolve to a `boolean` value, it uses the [truthy logic](../truthy-logic)\n:::",
    inputSchema: { type: "array", required: true, description: "Array of elements" },
    outputSchemaTemplate: { type: "array", description: "Same items type as input" },
    pipedType: true,
    arguments: [
      {
        name: "by",
        description: "A predicate transformer for an element",
        type: "transformer",
        position: 0,
        required: true,
        transformerArguments: [
          { name: "##current", type: "any", position: 0, description: "Current element" },
          { name: "##index", type: "integer", position: 1, description: "Current index" },
        ],
      },
    ],
  },
  find: {
    description: "Find the first element in a specified array that satisfy the predicate transformer",
    usageNotes:
      ":::info\npredicate `by` should resolve to a `boolean` value, it uses the [truthy logic](../truthy-logic)\n:::",
    inputSchema: { type: "array", required: true, description: "Array of elements" },
    outputSchemaTemplate: { type: "object", description: "Same as found element" },
    arguments: [
      {
        name: "by",
        description: "A predicate transformer for an element",
        type: "transformer",
        position: 0,
        default: "##current",
        transformerArguments: [
          { name: "##current", type: "any", position: 0, description: "Current element" },
          { name: "##index", type: "integer", position: 1, description: "Current index" },
        ],
      },
    ],
  },
  findindex: {
    description:
      "Find the index of the first element in a specified array that satisfy the predicate transformer.\n\nIf none of the elements satisfy the predicate the result will be `-1`.",
    usageNotes:
      ":::info\npredicate `by` should resolve to a `boolean` value, it uses the [truthy logic](../truthy-logic)\n:::",
    inputSchema: { type: "array", required: true, description: "Array of elements" },
    outputSchema: { type: "number" },
    arguments: [
      {
        name: "by",
        description: "A predicate transformer for an element",
        type: "transformer",
        position: 0,
        default: "##current",
        transformerArguments: [
          { name: "##current", type: "any", position: 0, description: "Current element" },
          { name: "##index", type: "integer", position: 1, description: "Current index" },
        ],
      },
    ],
  },
  flat: {
    description: "Flatten an array of arrays (non array elements will remain)",
    notes: ":::note\nAll `null` elements are removed from result\n:::",
    inputSchema: { type: "array", required: true, description: "Array of arrays / elements" },
    outputSchemaTemplate: { type: "array" },
  },
  flatten: {
    description: "Flattens an object into a flat dot seperated list of entries",
    inputSchema: { type: "object", required: true, description: "any object" },
    outputSchema: { type: "object" },
    arguments: [
      { name: "target", description: "A target to merge into", type: "object", position: 0, default: null },
      { name: "prefix", description: "A prefix to add to the base", type: "string", position: 1, default: "" },
      {
        name: "array_prefix",
        description:
          'Sets how array elements indices should be prefixed (If not set, elements will be prefixed like ` ${index}`).\n - Set to `"#null"` to not flatten arrays.',
        type: "string",
        position: 2,
        default: "$",
      },
    ],
  },
  form: {
    description: "Converts an object to Form URL-Encoded string (a.k.a Query String)",
    notes:
      "- Array values will be treated as multiple values for the same key (so the key will be duplicated in the result for each of the values)",
    inputSchema: { type: "object", required: true },
    outputSchemaTemplate: { type: "string", description: "form url-encoded string" },
    outputSchema: { type: "string" },
  },
  formparse: {
    description: "Parses a Form URL-Encoded string to `object`",
    notes:
      "- Every element will have 2 forms in the result object:\n  - Singular with its original query name (e.g. `q`)\n  - Plural with its name suffixed with `$$` (e.g. `q$$`)",
    inputSchema: { type: "string", required: true, description: "form url-encoded string" },
    outputSchema: { type: "object" },
  },
  group: {
    description:
      "Groups an array of entries into a map of `key -&gt; array` by a specified transformer (with optional nested grouping)",
    notes: "Sorts elements of an array",
    inputSchema: { type: "array", required: true, description: "Array of elements" },
    outputSchemaTemplate: { type: "object", description: "Map of `key -&gt; array`" },
    outputSchema: { type: "object", additionalProperties: { type: "array" } },
    arguments: [
      {
        name: "by",
        description: "A transformer to extract a property to group by",
        type: "transformer",
        position: 0,
        required: true,
        transformerArguments: [{ name: "##current", type: "any", position: 0, description: "Current element" }],
      },
      {
        name: "order",
        description: "Direction of ordering (ascending / descending)",
        type: "enum",
        enum: ["ASC", "DESC"],
        position: 1,
        default: "ASC",
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
        name: "then",
        description:
          'An array of subsequent grouping. When previous sort had no difference (only when `by` specified)\n`{ "by": ..., "order": ..., "type": ...}` // same 3 fields as above (`by` is required)',
        type: "array",
        position: 3,
      },
    ],
  },
  if: {
    description:
      "Conditionally returns a value, if the evaluation of the condition argument is truthy (using the [Truthy logic](../truthy-logic)).\nA fallback value (if condition evaluates to false) is optional",
    usageNotes:
      ":::note\nAlternative form is available using \n```transformers \n{\n" +
      '  "$$if": [ /* condition */, /* then */, /* else ? */]\n' +
      "}\n```\nIf `then` is used, the primary argument is treated as a condition\n:::",
    inputSchema: {
      type: "any",
      required: true,
      description: "Condition",
    },
    arguments: [
      { name: "then", required: true, description: "Value to return if condition is true", type: "any", position: 0 },
      { name: "else", description: "Value to return if condition is false", type: "any", position: 1, default: null },
    ],
  },
  indexof: {
    description: "Find the index of the first element that matches the specified value.",
    inputSchema: { type: "array", required: true, description: "Array of elements" },
    outputSchema: { type: "number" },
    arguments: [
      {
        name: "of",
        description: "The value to look for",
        type: "any",
        position: 0,
        required: true,
      },
    ],
  },
  is: {
    description: "Checks value for one or more predicates (all predicates must be satisfied)",
    usageNotes:
      ":::note\n**Inline form** supports only `that` and `op` arguments.\n:::\n\n:::tip\n`gt`/`gte`/`lt`/`lte` - Uses the [Comparison logic](../comparison-logic)\n:::",
    inputSchema: { type: "object", required: true, description: "Value to check against" },
    outputSchema: { type: "boolean" },
    arguments: [
      {
        name: "op",
        description: "A type of check to do exclusively (goes together with `that`)",
        type: "enum",
        enum: ["IN", "NIN", "EQ", "=", "==", "NEQ", "!=", "<>", "GT", ">", "GTE", ">=", "LT", "<", "LTE", "<="],
        position: 0,
      },
      { name: "that", description: "", type: "any", position: 1 },
      { name: "in", description: "Array of values the input should be part of", type: "array" },
      { name: "nin", description: "Array of values the input should **NOT** be part of", type: "array" },
      { name: "eq", description: "A value the input should be equal to", type: "any" },
      { name: "neq", description: "A value the input should **NOT** be equal to", type: "any" },
      { name: "gt", description: "A value the input should be greater than (input > value)", type: "any" },
      {
        name: "gte",
        description: "A value the input should be greater than or equal (input >= value)",
        type: "any",
      },
      { name: "lt", description: "A value the input should be lower than (input < value)", type: "any" },
      {
        name: "lte",
        description: "A value the input should be lower than or equal (input <= value)",
        type: "any",
      },
    ],
  },
  isnull: {
    description: "Returns true if value does not exist or equal to null",
    inputSchema: { type: "object", required: true, description: "value to check against" },
    outputSchema: { type: "boolean" },
  },
  join: {
    description:
      'Joins an array of input as strings with an optional delimiter (default is `""`), prefix and suffix. `null` values are omitted',
    inputSchema: { type: "array", required: true, description: "Array of elements" },
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
  jsonparse: {
    description: "Parses input as JSON string",
    inputSchema: { type: "string", required: true, description: "JSON serialized string" },
    outputSchemaTemplate: { type: "object", description: "Parsed value" },
    outputSchema: { type: "object" },
  },
  jsonpatch: {
    description: "Apply patches defined by JSON Patch RFC-6902",
    argumentsNotes:
      '#### JSON Patch Operations\n\n| Operation | Example                                                                                           |\n|-----------|---------------------------------------------------------------------------------------------------|\n| Add       | `{ "op":"add", "path":"/...", "value":"..." }`                                                    |\n| Remove    | `{ "op":"remove", "path":"/..." }`                                                                |\n| Replace   | `{ "op":"replace", "path":"/...", "value":"..." }`                                                |\n| Move      | `{ "op":"move", "path":"/...", "from":"/..." }`                                                   |\n| Copy      | `{ "op":"copy", "path":"/...", "from":"/..." }`                                                   |\n| Test      | `{ "op":"test", "path":"/...", "value":"..." }` (if test fails, the function result will be null) |',
    inputSchema: { type: "any", required: true, description: "Object to patch" },
    outputSchemaTemplate: { type: "object", description: "Operations output" },
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
    inputSchema: { type: "object", required: true, description: "Object to query" },
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
          "Configurations for the resolver, A list of options [by jayway](https://github.com/json-path/JsonPath?tab=readme-ov-file#tweaking-configuration)",
        type: "string[]",
        position: 1,
        default: ["SUPPRESS_EXCEPTIONS"],
      },
    ],
  },
  jsonpointer: {
    description: "Apply mutations or queries over an object using JSON Pointer (defined by RFC-6901)",
    inputSchema: { type: "object", required: true, description: "Object to query" },
    outputSchemaTemplate: { type: "object", description: "Query result" },
    outputSchema: { type: "object" },
    arguments: [
      {
        name: "op",
        description: "Operation",
        type: "enum",
        enum: ["GET", "SET", "REMOVE"],
        position: 0,
        required: true,
      },
      {
        name: "pointer",
        description: "JSON Pointer to apply operation on (as defined by RFC-6901)",
        type: "string",
        position: 1,
        required: true,
      },
      {
        name: "value",
        description: "Value to use (when operation is `SET`)",
        type: "any",
        position: 2,
      },
    ],
  },
  jwtparse: {
    description: "Parses JWT tokens and returns their payload",
    notes: ":::caution\nThis function does not **validate** the JWT. Only returns its payload (claims).\n:::",
    inputSchema: { type: "string", required: true, description: "JWT string" },
    outputSchemaTemplate: { type: "object", description: "Token's payload" },
    outputSchema: { type: "object" },
  },
  length: {
    description: "Returns the length of a value",
    inputSchema: { type: "any", required: true, description: "Value to check length of (`string`/`object`/`array`)" },
    outputSchema: { type: "integer" },
    arguments: [
      {
        name: "type",
        description:
          "Restrict the type of value to check length of (if specified type no detected the result will be `null`)",
        type: "enum",
        enum: ["AUTO", "STRING", "ARRAY", "OBJECT"],
        position: 0,
        default: "AUTO",
      },
      {
        name: "default_zero",
        description: "Whether to return 0 instead of `null` (on any kind of issue)",
        type: "boolean",
        position: 1,
        default: false,
      },
    ],
  },
  long: {
    description: "Converts to integer, all decimal digits are truncated",
    inputSchema: { type: "any", description: "Value to convert" },
    outputSchema: { type: "integer", $comment: "Long" },
  },
  lookup: {
    description: "Joins multiple arrays of objects by a dynamic condition (transformer) on each pair of matches",
    usageNotes:
      '```transformers\n{\n  "$$lookup": [ /* values */ ],\n  "using": [\n    { "with": [ /* values 2 */], "as": /* name 2 */, "on": /* Transformer */ },\n    { "with": [ /* values 3 */], "as": /* name 3 */, "on": /* Transformer */ },\n    ...\n  ],\n  "to": /* Transformer */\n}\n```',
    inputSchema: { type: "array", required: true, description: "Main array of elements" },
    outputSchemaTemplate: {
      type: "array",
      description: "of type of `to`'s result or the merge of both primary array's item and `with`'s item",
    },
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
        description:
          "Transformer to map each pair of elements to its value in the result array\n\nDefault behavior is to merge `##current` with `##{as}` (append & overwrite)",
        type: "transformer",
        position: 1,
        transformerArguments: [
          { name: "##current", type: "any", position: 0, description: "Current element" },
          { name: "##{as}", type: "any", position: 1, description: "Matched element from `with`" },
          { name: "...", type: "any", position: 2 },
        ],
      },
      {
        name: "using[].with",
        type: "array",
        required: true,
        description: "Array of elements to match with",
      },
      {
        name: "using[].as",
        type: "string",
        required: true,
        description: "The name the elements from this entry will be referred as (`#{as}`)",
      },
      {
        name: "using[].on",
        type: "transformer",
        required: true,
        description:
          "Evaluated condition on when to match an element from the main array and with array (uses the [Truthy logic](../truthy-logic))",
        transformerArguments: [
          { name: "##current", type: "any", position: 0, description: "Current element" },
          { name: "##index", type: "any", position: 1, description: "Current index" },
          {
            name: "##{as}",
            type: "any",
            position: 2,
            description: "the matched element from the array (replace `{as}` with the name provided in `as` argument)",
          },
        ],
      },
    ],
  },
  lower: {
    description: "Converts a string to all lowercase letters",
    inputSchema: { type: "string", required: true, description: "Input string" },
    outputSchema: { type: "string" },
  },
  map: {
    description: "Returns a mapped array applying the transformer on each of the elements",
    usageNotes:
      ":::note\nAlternative form is available using \n```transformers \n{\n" +
      '  "$$map": [ /* values */, /* to */ ]\n' +
      "}\n```\nIf `to` is used, the primary argument is treated only as values\n:::",
    inputSchema: { type: "array", required: true, description: "Array of elements" },
    outputSchemaTemplate: { type: "array", description: "of type of `to`'s result" },
    arguments: [
      {
        name: "to",
        description: "Transformer to map each element to its value in the result array",
        type: "transformer",
        position: 0,
        required: true,
        transformerArguments: [
          { name: "##current", type: "any", position: 0, description: "Current element" },
          { name: "##index", type: "integer", position: 1, description: "Current index" },
        ],
      },
    ],
  },
  match: {
    description: "Returns a matched substring from input by a pattern (and optionally group id)",
    inputSchema: { type: "string", required: true, description: "Input string" },
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
    inputSchema: { type: "string", required: true, description: "Input string" },
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
      type: "BigDecimal",
      required: true,
      description: "`op1` / `op` / `array` (`[op1, op, op2]` / `[op, op1, op2]`)",
    },
    usageNotes:
      '**OR**\n```transformers\n{\n  "$$math": [/* operator */, /* operand 1 */, /* operand 2 ? */]\n}\n```\n**OR**\n```transformers\n{\n  "$$math": [/* operand 1 */, /* operator */, /* operand 2 ? */]\n}\n```\n**OR**\n```transformers\n"$$math(<operand1>,<operator>,[operand2])"\n\n"$$math(<operator>,<operand1>,[operand2])"\n\n"$$math(<operator>,[operand2]):{operand1}"\n```',
    argumentsNotes:
      '#### Operators\n\n| `operator`                | Action                 | Example                     |\n|---------------------------|------------------------|-----------------------------|\n| `+`, `ADD`                | Addition               | `"$$math(2,+,3)"` = `5`     |\n| `-`, `SUB`, `SUBTRACT`    | Subtraction            | `"$$math(5,-,3)"` = `2`     |\n| `*`, `MUL`, `MULTIPLY`    | Multiplication         | `"$$math(2,*,3)"` = `6`     |\n| `/`, `DIV`, `DIVIDE`      | Division               | `"$$math(6,/,3)"` = `2`     |\n| `//`, `INTDIV`            | Integer division       | `"$$math(7,//,3)"` = `2`    |\n| `%`, `MOD`, `REMAINDER`   | Modulu                 | `"$$math(7,%,3)"` = `1`     |\n| `^`, `**`, `POW`, `POWER` | Power                  | `"$$math(2,^,3)"` = `8`     |\n| `&`, `AND`                | Bit-wise AND           | `"$$math(6,&,3)"` = `2`     |\n| `\\|`, `OR`      | Bit-wise OR            | `"$$math(6,OR,3)"` = `7`    |\n| `~`, `XOR`                | Bit-wise XOR           | `"$$math(6,~,3)"` = `5`     |\n| `<<`, `SHL`               | Shift left (bit-wise)  | `"$$math(6,>>,1)"` = `3`    |\n| `>>`, `SHR`               | Shift right (bit-wise) | `"$$math(6,<<,3)"` = `48`   |\n| `MIN`                     | Minimum                | `"$$math(MIN,4,2)"` = `2`   |\n| `MAX`                     | Maximum                | `"$$math(MAX,4,2)"` = `4`   |\n| `SQRT`                    | Square root            | `"$$math(SQRT,81)"` = `9`   |\n| `ROUND`                   | Round                  | `"$$math(ROUND,4.6)"` = `5` |\n| `FLOOR`                   | Floor                  | `"$$math(FLOOR,4.6)"` = `4` |\n| `CEIL`                    | Ceil                   | `"$$math(CEIL,4.2)"` = `5`  |\n| `ABS`                     | Absolute               | `"$$math(ABS,-10)"` = `10`  |\n| `NEG`, `NEGATE`           | Negation               | `"$$math(NEG,4)"` = `-4`    |\n| `SIG`, `SIGNUM`           | Sign Number            | `"$$math(SIG,-42)"` = `-1`  |\n',
    outputSchema: { type: "number", $comment: "BigDecimal" },
    argumentsAsInputSchema: true,
    arguments: [
      { name: "op1", description: "First operand", type: "BigDecimal", position: 0, required: true },
      {
        name: "op",
        description: "Math operation (operator)",
        type: "enum",
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
      {
        name: "op2",
        description: "Second operand or scale for `ROUND`/`FLOOR`/`CEIL`",
        type: "BigDecimal",
        position: 2,
        default: 0.0,
      },
    ],
  },
  max: {
    description: "Returns the max of all values in the array",
    inputSchema: { type: "array", required: true, description: "Array of elements" },
    outputSchemaTemplate: { type: "object", $comment: "Same as picked element" },
    arguments: [
      { name: "default", type: "any", description: "The default value to use for empty values", position: 0 },
      {
        name: "type",
        description: "Type of values to expect when ordering the input array",
        type: "enum",
        enum: ["AUTO", "STRING", "NUMBER", "BOOLEAN"],
        position: 1,
        default: "AUTO",
      },
      {
        name: "by",
        description: "A transformer to extract a property to compare by",
        type: "transformer",
        position: 2,
        transformerArguments: [{ name: "##current", type: "any", position: 0, description: "Current element" }],
      },
    ],
  },
  merge: {
    description: "Merge multiple objects into one. Allows deep merging and array concatenation.",
    inputSchema: { type: "array", required: true, description: "Array of objects to merge" },
    outputSchemaTemplate: { type: "object", description: "The merged schema of all specified objects" },
    outputSchema: { type: "object" },
    arguments: [
      {
        name: "deep",
        description: "Whether to merge objects deeply",
        type: "boolean",
        position: 0,
        default: false,
      },
      {
        name: "arrays",
        description: "Whether to concatenate arrays",
        type: "boolean",
        position: 1,
        default: false,
      },
    ],
  },
  min: {
    description: "Returns the min of all values in the array",
    inputSchema: { type: "array", required: true, description: "Array of elements" },
    outputSchemaTemplate: { type: "object", $comment: "Same as picked element" },
    arguments: [
      { name: "default", type: "any", description: "The default value to use for empty values", position: 0 },
      {
        name: "type",
        description: "Type of values to expect when ordering the input array",
        type: "enum",
        enum: ["AUTO", "STRING", "NUMBER", "BOOLEAN"],
        position: 1,
        default: "AUTO",
      },
      {
        name: "by",
        description: "A transformer to extract a property to compare by",
        type: "transformer",
        position: 2,
        transformerArguments: [{ name: "##current", type: "any", position: 0, description: "Current element" }],
      },
    ],
  },
  normalize: {
    description: "Replace special characters forms with their simple form equivalent (removing marks by default)",
    notes:
      '- Allows post-processing over Java\'s normalizer algorithm result\n#### Post Operations\n- `ROBUST` - Try to return the most of similar letters to latin, replaced to their latin equivalent, including:\n  - Removing combining diacritical marks (works with NFD/NFKD which leaves the characters decomposed)\n  - Stroked (and others which are not composed) (i.e. "ĐŁłŒ" -> "DLlOE")\n  - Replacing (with space) and trimming white-spaces\n',
    inputSchema: { type: "string", required: true, description: "String to normalize" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "form",
        description:
          "Normalizer Form (as described in Java's documentation. Default is NFKD; Decompose for compatibility)",
        type: "enum",
        enum: ["NFKD", "NFKC", "NFD", "NFC"],
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
    description:
      "Returns the opposite of the input's boolean evaluated value (this returns the opposite of [$$boolean](boolean))",
    inputSchema: { type: "any" },
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
  numberformat: {
    description: "Formats a number",
    inputSchema: { type: "BigDecimal", required: true, description: "Number to format" },
    outputSchema: { type: "string" },
    subfunctions: [
      {
        if: [{ argument: "type", equals: "NUMBER" }],
        then: {
          description: 'Formats a number using the default pattern `"#,##0.000"`',
          arguments: [
            {
              name: "type",
              description: "Type of output format",
              type: "const",
              position: 0,
              const: "NUMBER",
            },
          ],
        },
      },
      {
        if: [{ argument: "type", equals: "DECIMAL" }],
        then: {
          description: "Formats a number to a decimal representation",
          arguments: [
            {
              name: "type",
              description: "Type of output format",
              type: "const",
              position: 0,
              const: "DECIMAL",
            },
            {
              name: "locale",
              description: "Locale to use (language and country specific formatting)",
              type: "string",
              position: 1,
              default: "en-US",
            },
            {
              name: "pattern",
              description: "See [tutorial](https://docs.oracle.com/javase/tutorial/i18n/format/decimalFormat.html)",
              type: "string",
              position: 2,
              default: "#0.00",
            },
            {
              name: "grouping",
              description: "A custom character to be used for grouping",
              type: "string",
              position: 3,
              default: ",",
            },
            {
              name: "decimal",
              description: "A custom character to be used for decimal point",
              type: "string",
              position: 4,
              default: ".",
            },
          ],
        },
      },
      {
        if: [{ argument: "type", equals: "CURRENCY" }],
        then: {
          description: "Formats a number as currency",
          arguments: [
            {
              name: "type",
              description: "Type of output format",
              type: "const",
              position: 0,
              const: "CURRENCY",
            },
            {
              name: "locale",
              description: "Locale to use (language and country specific formatting)",
              type: "string",
              position: 1,
              default: "en-US",
            },
            {
              name: "currency",
              description:
                "Currency to use in format ([ISO-4217 currency code](https://www.iso.org/iso-4217-currency-codes.html)) (default would be based on locale)",
              type: "string",
              position: 2,
            },
          ],
        },
      },
      {
        if: [{ argument: "type", equals: "PERCENT" }],
        then: {
          description: "Formats a number as percent",
          arguments: [
            {
              name: "type",
              description: "Type of output format",
              type: "const",
              position: 0,
              const: "PERCENT",
            },
            {
              name: "locale",
              description: "Locale to use (language and country specific formatting)",
              type: "string",
              position: 1,
              default: "en-US",
            },
          ],
        },
      },
      {
        if: [{ argument: "type", equals: "INTEGER" }],
        then: {
          description: "Formats a number as an integer",
          arguments: [
            {
              name: "type",
              description: "Type of output format",
              type: "const",
              position: 0,
              const: "INTEGER",
            },
            {
              name: "locale",
              description: "Locale to use (language and country specific formatting)",
              type: "string",
              position: 1,
              default: "en-US",
            },
          ],
        },
      },
      {
        if: [{ argument: "type", equals: "COMPACT" }],
        then: {
          description: "Formats a number in compact style",
          arguments: [
            {
              name: "type",
              description: "Type of output format",
              type: "const",
              position: 0,
              const: "COMPACT",
            },
            {
              name: "locale",
              description: "Locale to use (language and country specific formatting)",
              type: "string",
              position: 1,
              default: "en-US",
            },
            {
              name: "compact_style",
              description: "Type of compacting format",
              type: "enum",
              enum: ["SHORT", "LONG"],
              position: 2,
              default: "SHORT",
            },
          ],
        },
      },
      {
        if: [{ argument: "type", equals: "BASE" }],
        then: {
          description: "Formats a number in a specific radix",
          arguments: [
            {
              name: "type",
              description: "Type of output format",
              type: "const",
              position: 0,
              const: "BASE",
            },
            {
              name: "radix",
              description: "Radix to be used for formatting input",
              type: "integer",
              position: 1,
              default: 10,
            },
          ],
        },
      },
    ],
    arguments: [
      {
        name: "type",
        description: "Type of output format",
        type: "enum",
        enum: ["NUMBER", "DECIMAL", "CURRENCY", "PERCENT", "INTEGER", "COMPACT", "BASE"],
        position: 0,
        default: "NUMBER",
      },
    ],
  },
  numberparse: {
    description: "Parses a number from string",
    inputSchema: { type: "string", description: "String containing a number" },
    outputSchema: { type: "number", $comment: "BigDecimal" },
    subfunctions: [
      {
        if: [{ argument: "pattern", equals: "BASE" }],
        then: {
          description: "Parses a number from string in a specific radix",
          arguments: [
            {
              name: "pattern",
              description: "Specify parse in a specific base",
              type: "const",
              position: 0,
              const: "BASE",
            },
            {
              name: "radix",
              description: "Radix to be used for parsing input",
              type: "integer",
              position: 1,
              default: 10,
            },
          ],
        },
      },
    ],
    arguments: [
      {
        name: "pattern",
        description:
          "See [tutorial](https://docs.oracle.com/javase/tutorial/i18n/format/decimalFormat.html).\n`DecimalFormat` pattern / `BASE`.",
        type: "string",
        position: 0,
        default: "#0.00",
      },
      {
        name: "locale",
        description: "Locale to use (language and country specific formatting)",
        type: "string",
        position: 1,
        default: "en-US",
      },
      {
        name: "grouping",
        description: "A custom character to be used for grouping",
        type: "string",
        position: 2,
        default: ",",
      },
      {
        name: "decimal",
        description: "A custom character to be used for decimal point",
        type: "string",
        position: 3,
        default: ".",
      },
    ],
  },
  object: {
    description: "Reduces an array of entries into an object",
    notes: ":::note\n*Entry is in the form of `[ key, value ]`\n:::",
    inputSchema: { type: "array", required: true, description: "Array of entries" },
    outputSchema: { type: "object" },
  },
  or: {
    description:
      "Evaluates to `true` if any of the values provided will evaluate to `true` (using the [Truthy logic](../truthy-logic))",
    inputSchema: { type: "array", required: true, description: "Values to check" },
    outputSchema: { type: "boolean" },
  },
  pad: {
    description: "Pad a provided string with certain character(s) repeated until a certain width of output string",
    notes: "(Strings longer than `width` will be returned as-is)",
    inputSchema: { type: "any", description: "Value to pad" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "direction",
        description: "On which side of the input to pad",
        type: "enum",
        enum: ["LEFT", "START", "RIGHT", "END"],
        position: 0,
        required: true,
      },
      {
        name: "width",
        description: "What is the maximum length of the output string",
        type: "integer",
        position: 1,
        required: true,
      },
      {
        name: "pad_string",
        description: "The character(s) to pad with",
        type: "string",
        position: 2,
        default: "0",
      },
    ],
  },
  partition: {
    description: "Partition an array to multiple constant size arrays",
    inputSchema: { type: "array", required: true, description: "Array of elements" },
    outputSchemaTemplate: { type: "array", items: { type: "array" }, description: "of same items type as input" },
    arguments: [
      { name: "size", description: "The size of each partition", type: "integer", position: 0, default: 100 },
    ],
  },
  range: {
    description: "Creates an array with a sequence of numbers starting with `start` up-to `end` in steps of `step`",
    usageNotes:
      ":::note\nAlternative form is available using \n```transformers \n{\n" +
      '  "$$range": [ /* start */, /* end */, /* step ? */]\n' +
      "}\n```\nIf `start` is used, the primary argument is ignored and can be of any value\n:::",
    argumentsAsInputSchema: true,
    outputSchema: { type: "array", items: { type: "number", $comment: "BigDecimal" } },
    arguments: [
      { name: "start", description: "First value", type: "BigDecimal", position: 0, required: true },
      {
        name: "end",
        description: "Max value to appear in sequence",
        type: "BigDecimal",
        position: 1,
        required: true,
      },
      {
        name: "step",
        description: "Step to add on each iteration to the previous value in the sequence",
        type: "BigDecimal",
        position: 2,
        default: 1.0,
      },
    ],
  },
  raw: {
    description: "Returns the input as-is without interpreting transformers",
    inputSchema: { type: "any", required: true, description: "Value to return as-is" },
    outputSchemaTemplate: { type: "object", description: "Same type as input" },
  },
  reduce: {
    description: "Reduce an array with an initial value (`identity`) and a context transformer to a single value",
    inputSchema: { type: "array", required: true, description: "Array of elements" },
    outputSchemaTemplate: { type: "object", description: "The reduced value (type of `to` or `identity`)" },
    arguments: [
      {
        name: "to",
        description: "Transformer to apply on each element (with last accumulation) to get the next accumulation",
        type: "transformer",
        position: 0,
        required: true,
        transformerArguments: [
          { name: "##accumulator", type: "any", position: 0, description: "Previously accumulated value" },
          { name: "##current", type: "any", position: 1, description: "Current element" },
          { name: "##index", type: "integer", position: 2, description: "Current index" },
        ],
      },
      {
        name: "identity",
        description: "Initial value to start the accumulation with",
        type: "any",
        position: 1,
        required: true,
      },
    ],
  },
  repeat: {
    description: "Creates an array with the specified value repeated `count` times",
    inputSchema: { type: "any", required: true, description: "Value to repeat" },
    outputSchemaTemplate: { type: "array", description: "of same type as input" },
    arguments: [
      {
        name: "count",
        description: "The amount of times to repeat the value",
        type: "integer",
        position: 0,
        required: true,
      },
    ],
  },
  replace: {
    description: "Search and replaces a substring in the given input",
    inputSchema: { type: "string", required: true },
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
        name: "replacement",
        description:
          "Value to replace each match (or only first if type is `REGEX-FIRST`), when using regular expression can use group matches (e.g. `$1`) (Note: to escape `$` if starting with it)",
        type: "string",
        position: 1,
        default: "",
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
        name: "from",
        description: "At what index in the string the search should start from",
        type: "integer",
        position: 3,
        default: 0,
      },
    ],
  },
  reverse: {
    description: "Reverses the order of elements in an array",
    inputSchema: { type: "array", required: true, description: "Array of elements" },
    outputSchemaTemplate: { type: "array", description: "Same type as input array" },
    pipedType: true,
  },
  slice: {
    description: "Gets a slice of an array by indices (negative begin index will slice from the end)",
    inputSchema: { type: "array", required: true, description: "Array to fetch from" },
    outputSchemaTemplate: {
      type: "object",
      description: "Same type as input array",
    },
    pipedType: true,
    arguments: [
      {
        name: "begin",
        description: "Index of first element to slice from (if negative, counts from the end of the array)",
        type: "integer",
        position: 0,
        required: true,
      },
      {
        name: "end",
        description: "Index of last element to slice to (if negative, counts from the end of the array)",
        type: "integer",
        position: 1,
        default: Infinity,
      },
    ],
  },
  some: {
    aliases: ["any"],
    description: "Checks if any elements in an array satisfy a predicate",
    usageNotes:
      ":::info\npredicate `by` should resolve to a `boolean` value, it uses the [truthy logic](../truthy-logic)\n:::",
    inputSchema: { type: "array", required: true, description: "Array of elements" },
    outputSchema: { type: "boolean" },
    arguments: [
      {
        name: "by",
        description: "A predicate transformer for an element",
        type: "transformer",
        position: 0,
        required: true,
        transformerArguments: [{ name: "##current", type: "any", position: 0, description: "Current element" }],
      },
    ],
  },
  sort: {
    description: "Sorts elements of an array",
    inputSchema: { type: "array", required: true, description: "Array of elements" },
    outputSchemaTemplate: { type: "array", description: "Same type as input array" },
    pipedType: true,
    arguments: [
      {
        name: "by",
        description: "A transformer to extract a property to sort by",
        type: "transformer",
        position: 0,
        transformerArguments: [{ name: "##current", type: "any", position: 0, description: "Current element" }],
        default: "##current",
      },
      {
        name: "order",
        description: "Direction of ordering (ascending / descending)",
        type: "enum",
        enum: ["ASC", "DESC"],
        position: 1,
        default: "ASC",
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
        name: "then",
        description:
          "An array of secondary sorting in case previous sorting returned equal, first being the root fields",
        type: "array",
        position: 3,
      },
      {
        name: "then[].by",
        description: "A transformer to extract a property to sort by",
        type: "array",
        required: true,
        transformerArguments: [{ name: "##current", type: "any", position: 0, description: "Current element" }],
      },
      {
        name: "then[].order",
        description: "Direction of ordering (ascending / descending)",
        type: "enum",
        enum: ["ASC", "DESC"],
        default: "ASC",
      },
      {
        name: "then[].type",
        description: "Type of values to expect when ordering the input array",
        type: "enum",
        enum: ["AUTO", "STRING", "NUMBER", "BOOLEAN"],
        default: "AUTO",
      },
    ],
  },
  split: {
    description: "Splits a string using a given delimiter/regex",
    inputSchema: { type: "string", required: true, description: "String to split" },
    outputSchema: { type: "array", items: { type: "string" } },
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
        description:
          "Limit the amount of elements returned (and by that, the amount the pattern get matched).\n - `0` - Means no limit.",
        type: "integer",
        position: 1,
        default: 0,
      },
    ],
  },
  string: {
    description: "Converts to string (if `json` set to `true`, will convert `null` and strings also as JSON strings)",
    inputSchema: { type: "object", required: true, description: "Value to convert to string" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "json",
        description:
          "Whether to convert `null` and strings to json (otherwise, `null` stays `null` and strings are returned as-is)",
        type: "boolean",
        position: 0,
        default: false,
      },
    ],
  },
  substring: {
    description: "Gets a slice of a string by indices (negative begin index will slice from the end)",
    inputSchema: { type: "string", required: true, description: "String to slice" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "begin",
        description: "Index of first character to slice from (if negative, counts from the end of the string)",
        type: "integer",
        position: 0,
        required: true,
      },
      {
        name: "end",
        description: "Index of last character to slice to (if negative, counts from the end of the string)",
        type: "integer",
        position: 1,
      },
    ],
  },
  sum: {
    description: "Returns the sum of all values in an array",
    inputSchema: { type: "array", required: true, description: "Array to sum" },
    outputSchema: { type: "number", $comment: "BigDecimal" },
    arguments: [
      {
        name: "default",
        description: "The default value to use for empty values",
        type: "BigDecimal",
        position: 0,
        default: 0.0,
      },
      {
        name: "by",
        description: "A transformer to extract a property to sum by",
        type: "transformer",
        position: 1,
        transformerArguments: [{ name: "##current", type: "any", position: 0, description: "Current element" }],
      },
    ],
  },
  switch: {
    description:
      "Switch-case expression. Value is compared to each of the keys in `cases` and a matching **key** will result with its **value**, otherwise `default` value or `null` will be returned.",
    inputSchema: { type: "any", required: true, description: "Value to test" },
    outputSchemaTemplate: { type: "object", description: "Same as `default` value or one of the `cases` values" },
    arguments: [
      {
        name: "cases",
        description: "A map of cases (string to value)",
        type: "object",
        position: 0,
        required: true,
      },
      {
        name: "default",
        description: "Fallback value in case no match to any key in cases",
        type: "any",
        position: 1,
        default: null,
      },
    ],
  },
  template: {
    description: "Renders a specified text template with the given input referencing a specified payload.",
    argumentsNotes:
      "#### * Different Types of default parameter resolving options\n| Type               | Description                                                      |\n|--------------------|------------------------------------------------------------------|\n| `UNIQUE` (default) | Each instance of a parameter is resolved to its explicit default |\n| `FIRST_VALUE`      | The first default found for the parameter is used by all         |\n| `LAST_VALUE`       | The last default found is used by all                            |\n",
    inputSchema: { type: "string", required: true, description: "The text template to render" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "payload",
        description: "Additional context, referred to as `##current` from the template",
        type: "any",
        position: 0,
      },
      {
        name: "default_resolve",
        description: "Resolve default value based on previous default values or not",
        type: "enum",
        position: 1,
        enum: ["UNIQUE", "FIRST_VALUE", "LAST_VALUE"],
        default: "UNIQUE",
      },
      {
        name: "url_encode",
        description: "URL encode parameters",
        type: "boolean",
        position: 2,
        default: false,
      },
    ],
  },
  test: {
    description: "Checks if a string matches a certain pattern",
    inputSchema: { type: "string", required: true, description: "String to test" },
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
    inputSchema: { type: "object", required: true, description: "Input value" },
    outputSchemaTemplate: { type: "object", description: "Transformed value" },
    arguments: [
      {
        name: "to",
        description: "Transformer to apply on input",
        type: "transformer",
        position: 0,
        required: true,
        transformerArguments: [{ name: "##current", type: "any", position: 0, description: "Current element" }],
      },
    ],
  },
  trim: {
    description: "Removes whitespaces from sides of string",
    argumentsNotes:
      "#### * Different Types of trimming\n| Type             | Java equivalent function  |\n|------------------|---------------------------|\n| `BOTH` (default) | `String::strip()`         |\n| `START`          | `String::stripLeading()`  |\n| `END`            | `String::stripTrailing()` |\n| `INDENT`         | `String::stripIndent()`   |\n| `JAVA`           | `String::trim()`          |",
    inputSchema: { type: "string", required: true, description: "String to trim" },
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
    description: "Accepts an object with dot separated field names and merges them into an hierarchical object",
    inputSchema: { type: "object", required: true, description: "Object with dot separated field names" },
    outputSchema: { type: "object" },
    arguments: [
      { name: "target", description: "A target to merge into", type: "object", position: 0, default: null },
      {
        name: "path",
        description: "The root path in the target to merge into",
        type: "string",
        position: 1,
        default: null,
      },
    ],
  },
  upper: {
    description: "Converts a string to all uppercase letters",
    inputSchema: { type: "string", required: true, description: "Input string" },
    outputSchema: { type: "string" },
  },
  uriparse: {
    description: "Parses a URI to its components",
    inputSchema: {
      type: "string",
      required: true,
      description: "A URI formatted string",
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
    inputSchema: { type: "string", required: true, description: "URL encoded string" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "charset",
        description: "Character encoding to use",
        type: "enum",
        enum: ["UTF-8", "UTF-16"],
        position: 0,
        default: "UTF-8",
      },
    ],
  },
  urlencode: {
    description: "URL encodes as string",
    inputSchema: { type: "string", required: true, description: "String to encode" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "charset",
        description: "Character encoding to use",
        type: "enum",
        enum: ["UTF-8", "UTF-16"],
        position: 0,
        default: "UTF-8",
      },
    ],
  },
  uuid: {
    description: "Format and transform UUID",
    notes:
      ":::info\nInput must be a UUID in standard string format (RFC 4122; with hyphens), can be used in conjunction with `#uuid`\n:::",
    argumentsNotes:
      '- `NO_HYPHENS` (can also be specified as `N`) - Same as `CANONICAL` with hyphens removed\n- `BASE36` (can also be specified as `B36`) - alphanumeric alphabet\n- `BASE62` (can also be specified as `B62`) - alphanumeric alphabet, case sensitive\n- `BASE64` (can also be specified as `B64`) - "URL and Filename safe Base64 Alphabet"\n- `V3` - Consider input as name and generate a UUIDv3 (name-based, RFC 4122) (namespace optionally used)\n- `V5` - Consider input as name and generate a UUIDv5 (name-based, RFC 4122) (namespace optionally used)\n',
    inputSchema: {
      type: "string",
      description: "UUID",
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
  value: {
    description: "Returns the value if it passes the [truthy logic](../truthy-logic), or `null` otherwise",
    inputSchema: { type: "any", description: "Any value" },
    outputSchemaTemplate: { type: "object", description: "Same type as input or `null`" },
  },
  wrap: {
    description: "Wrap an input string with `prefix` and `suffix`",
    inputSchema: { type: "string", required: true, description: "String to wrap" },
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
    notes: "Optionally runs an XSLT over the result before returning it",
    inputSchema: { type: "object", required: true, description: '"XML structured" JSON object' },
    outputSchemaTemplate: { type: "string", description: "XML String" },
    outputSchema: { type: "string" },
    arguments: [
      {
        name: "root",
        description: "Name for a wrapper element (e.g. an array was passed and a container is needed)",
        type: "string",
        position: 0,
        default: null,
      },
      {
        name: "xslt",
        description: "XSLT document to transform xml created from input",
        type: "string",
        position: 1,
        default: null,
      },
      {
        name: "indent",
        description: "Whether to pretty print the XML",
        type: "boolean",
        position: 2,
        default: false,
      },
    ],
  },
  xmlparse: {
    description: "Parses an XML and converts it to an object",
    notes: "- Elements with text and attributes will be converted to objects with a `$content` field for the text",
    inputSchema: { type: "string", required: true, description: "XML string" },
    outputSchema: { type: "object" },
    arguments: [
      {
        name: "keep_strings",
        description: "Do not try to detect primitive types (e.g. numbers, boolean, etc)",
        type: "boolean",
        position: 0,
        default: false,
      },
      {
        name: "cdata_tag_name",
        description: "A key for the CDATA section",
        type: "string",
        position: 1,
        default: "$content",
      },
      {
        name: "convert_nil_to_null",
        description: 'If values with attribute `xsi:nil="true"` will be converted to `null`',
        type: "boolean",
        position: 2,
        default: false,
      },
      {
        name: "force_list",
        description: "Tag names that will always be parsed as arrays",
        type: "string[]",
        position: 3,
        default: [],
      },
    ],
  },
  xor: {
    description:
      "Evaluates to `true` if only one of the values provided will evaluate to `true` (using the [Truthy logic](../truthy-logic))",
    inputSchema: { type: "array", required: true, description: "Values to check" },
    outputSchema: { type: "boolean" },
  },
  yaml: {
    description: "Converts an object to YAML format",
    notes: ":::info\nStructure of output may very depending on platform.\n:::",
    inputSchema: { type: "object", required: true },
    outputSchemaTemplate: { type: "string", description: "YAML string" },
    outputSchema: { type: "string" },
  },
  yamlparse: {
    description: "Parses a YAML format to `object`",
    inputSchema: { type: "string", required: true, description: "YAML string" },
    outputSchema: { type: "object" },
  },
} as Record<EmbeddedTransformerFunction, FunctionDefinition>;
