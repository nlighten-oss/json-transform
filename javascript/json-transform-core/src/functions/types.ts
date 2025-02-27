import type { ParsedSchema, TypeSchema } from "@nlighten/json-schema-utils";

export enum EmbeddedTransformerFunction {
  and = "and", // boolean
  at = "at", // #SPECIAL
  avg = "avg", // number
  base64 = "base64", // string
  boolean = "boolean", // boolean
  coalesce = "coalesce", // #SPECIAL
  concat = "concat", // #SPECIAL
  contains = "contains", // boolean
  csv = "csv", // string
  csvparse = "csvparse", // array[] / object[] (arg based)
  date = "date", // string | number (arg based)
  decimal = "decimal", // number
  digest = "digest", // number | string (arg based)
  distinct = "distinct", // #SPECIAL
  entries = "entries", // [string, object]][]
  eval = "eval", // ?? (it might be possible if we can calculate the containing function, but definitely not always)
  every = "every", // boolean
  filter = "filter", // #SPECIAL
  find = "find", // #SPECIAL
  flat = "flat", // #SPECIAL
  flatten = "flatten", // object ??
  form = "form", // string
  formparse = "formparse", // object
  group = "group", // object ??
  if = "if", // #SPECIAL
  is = "is", // boolean
  isnull = "isnull", // boolean
  join = "join", // string
  jsonparse = "jsonparse", // object ??
  jsonpatch = "jsonpatch", // object ??
  jsonpath = "jsonpath", // object ??
  jsonpointer = "jsonpointer", // object ??
  jwtparse = "jwtparse", // object
  length = "length", // number
  long = "long", // number
  lookup = "lookup", // #SPECIAL
  lower = "lower", // string
  map = "map", // #SPECIAL
  match = "match", // string
  matchall = "matchall", // string[]
  math = "math", // number
  max = "max", // #SPECIAL
  merge = "merge", // #SPECIAL
  min = "min", // #SPECIAL
  normalize = "normalize", // string
  not = "not", // boolean
  numberformat = "numberformat", // string
  numberparse = "numberparse", // number
  object = "object", // object
  or = "or", // boolean
  pad = "pad", // string
  partition = "partition", // #SPECIAL
  range = "range", // number[]
  raw = "raw", // #SPECIAL
  reduce = "reduce", // #SPECIAL
  replace = "replace", // string
  reverse = "reverse", // #SPECIAL
  slice = "slice", // #SPECIAL
  some = "some", // boolean
  sort = "sort", // #SPECIAL
  split = "split", // string[]
  string = "string", // string
  substring = "substring", // string
  sum = "sum", // number
  switch = "switch", // #SPECIAL
  template = "template", // string
  test = "test", // boolean
  transform = "transform", // #SPECIAL
  trim = "trim", // string
  unflatten = "unflatten", // object ??
  upper = "upper", // string
  uriparse = "uriparse", // { ... }
  urldecode = "urldecode", // string
  urlencode = "urlencode", // string
  uuid = "uuid", // string
  value = "value", // #SPECIAL
  wrap = "wrap", // string
  xml = "xml", // string
  xmlparse = "xmlparse", // object
  xor = "xor", // boolean
  yaml = "yaml", // string
  yamlparse = "yamlparse", // object
}

export const EmbeddedTransformerFunctions = Object.keys(EmbeddedTransformerFunction) as EmbeddedTransformerFunction[];

export type Argument = {
  name: string; // !important for parsing
  position?: number; // !important for parsing
  required?: boolean; // !important for parsing
  default?: any; // !important for parsing
  description?: string;
  $comment?: string;
} & (
  | {
      type:
        | "boolean"
        | "string"
        | "integer"
        | "long"
        | "BigDecimal"
        | "array"
        | "object"
        | "transformer"
        | "string[]"
        | "any";
      enum?: undefined;
      transformerArguments?: undefined;
    }
  | {
      type: "enum";
      enum: string[];
    }
  | {
      type: "transformer";
      transformerArguments?: Argument[];
    }
  | {
      type: "const"; // for overrides documentation
      const: any;
    }
);

export type ArgumentCondition = {
  argument: string;
  equals: string;
};

export type ConditionalSubFunction<T extends FunctionDefinition> = {
  if: ArgumentCondition[]; // AND relationship
  then: T;
};

export type JsonTransformExample = {
  name: string;
  given: {
    input: any;
    // big-decimal means that 'input' is written as string but should be parsed as big-decimal
    inputFormat?: "csv" | "yaml" | "xml" | "big-decimal";
    definition: any;
  };
  expect: {
    equal?: any;
    notEqual?: any;
    isNull?: boolean;
    length?: number;
    type?: "array" | "bigint" | "boolean" | "function" | "number" | "object" | "string" | "symbol" | "undefined";
    ignoreOrder?: boolean;
    // big-decimal means that 'equal' is written as string but should be parsed as big-decimal
    format?: "date-time" | "big-decimal" | "url-search-params" | "csv" | "xml" | "yaml";
  };
};

export type FunctionDefinition = {
  aliases?: string[];
  inputSchema?: Omit<Argument, "name">;
  arguments?: Argument[];
  description: string;
  notes?: string;
  usageNotes?: string;
  argumentsNotes?: string;
  outputSchema?: TypeSchema;
  subfunctions?: ConditionalSubFunction<FunctionDefinition>[];
  /** For documentation purposes when output schema is calculated */
  outputSchemaTemplate?: TypeSchema;
  /** should specify the alternative function that should be used instead **/
  deprecatedInFavorOf?: string;
  /** If set, this function does not alter the type of its primary argument */
  pipedType?: boolean;
};

export type FunctionDescriptor = Omit<FunctionDefinition, "subfunctions"> & {
  subfunctions?: ConditionalSubFunction<FunctionDescriptor>[];
  custom?: boolean;
  /** (when outputSchema) Parsed in advance to get all possible paths */
  parsedOutputSchema?: ParsedSchema;
  defaultValues?: Record<string, any>;
  aliasTo?: string; // filled programmatically
};
