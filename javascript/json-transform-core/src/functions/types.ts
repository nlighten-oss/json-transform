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
  filter = "filter", // #SPECIAL
  find = "find", // #SPECIAL
  first = "first", // #SPECIAL
  flat = "flat", // #SPECIAL
  flatten = "flatten", // object ??
  form = "form", // string
  formparse = "formparse", // object
  group = "group", // object ??
  if = "if", // #SPECIAL
  is = "is", // boolean
  isnull = "isnull", // boolean
  join = "join", // string
  json = "json", // object ??
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
  name?: string;
  type: string;
  description?: string;
  enum?: string[];
  position?: number;
  required?: boolean;
  default?: any;
  $comment?: string;
};

export type FunctionDescriptor = {
  aliasTo?: string;
  inputSchema?: Argument;
  arguments?: Argument[];
  description: string;
  notes?: string;
  outputSchema?: TypeSchema;
  /** For documentation purposes when output schema is calculated */
  outputSchemaTemplate?: TypeSchema;
  deprecated?: string;
  /** If set, this function does not alter the type of its primary argument */
  pipedType?: boolean;
  custom?: boolean;
  argBased?: (args: Record<string, any>) => FunctionDescriptor;
  /** (when outputSchema) Parsed in advance to get all possible paths */
  parsedOutputSchema?: ParsedSchema;
};
