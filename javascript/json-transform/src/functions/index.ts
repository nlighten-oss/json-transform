import TransformerFunctionAnd from "./TransformerFunctionAnd";
import TransformerFunctionAt from "./TransformerFunctionAt";
import TransformerFunctionAvg from "./TransformerFunctionAvg";
import TransformerFunctionBase64 from "./TransformerFunctionBase64";
import TransformerFunctionBoolean from "./TransformerFunctionBoolean";
import TransformerFunctionCoalesce from "./TransformerFunctionCoalesce";
import TransformerFunctionConcat from "./TransformerFunctionConcat";
import TransformerFunctionContains from "./TransformerFunctionContains";
import TransformerFunctionCsv from "./TransformerFunctionCsv";
import TransformerFunctionCsvParse from "./TransformerFunctionCsvParse";
import TransformerFunctionDate from "./TransformerFunctionDate";
import TransformerFunctionDecimal from "./TransformerFunctionDecimal";
import TransformerFunctionDigest from "./TransformerFunctionDigest";
import TransformerFunctionDistinct from "./TransformerFunctionDistinct";
import TransformerFunctionEntries from "./TransformerFunctionEntries";
import TransformerFunctionEval from "./TransformerFunctionEval";
import TransformerFunctionFilter from "./TransformerFunctionFilter";
import TransformerFunctionFind from "./TransformerFunctionFind";
import TransformerFunctionFlat from "./TransformerFunctionFlat";
import TransformerFunctionFlatten from "./TransformerFunctionFlatten";
import TransformerFunctionForm from "./TransformerFunctionForm";
import TransformerFunctionFormParse from "./TransformerFunctionFormParse";
import TransformerFunctionGroup from "./TransformerFunctionGroup";
import TransformerFunctionIf from "./TransformerFunctionIf";
import TransformerFunctionIs from "./TransformerFunctionIs";
import TransformerFunctionIsNull from "./TransformerFunctionIsNull";
import TransformerFunctionJoin from "./TransformerFunctionJoin";
import TransformerFunctionJsonParse from "./TransformerFunctionJsonParse";
import TransformerFunctionJsonPatch from "./TransformerFunctionJsonPatch";
import TransformerFunctionJsonPath from "./TransformerFunctionJsonPath";
import TransformerFunctionJsonPointer from "./TransformerFunctionJsonPointer";
import TransformerFunctionJwtParse from "./TransformerFunctionJwtParse";
import TransformerFunctionLength from "./TransformerFunctionLength";
import TransformerFunctionLong from "./TransformerFunctionLong";
import TransformerFunctionLookup from "./TransformerFunctionLookup";
import TransformerFunctionLower from "./TransformerFunctionLower";
import TransformerFunctionMap from "./TransformerFunctionMap";
import TransformerFunctionMatch from "./TransformerFunctionMatch";
import TransformerFunctionMatchAll from "./TransformerFunctionMatchAll";
import TransformerFunctionMath from "./TransformerFunctionMath";
import TransformerFunctionMax from "./TransformerFunctionMax";
import TransformerFunctionMerge from "./TransformerFunctionMerge";
import TransformerFunctionMin from "./TransformerFunctionMin";
import TransformerFunctionNormalize from "./TransformerFunctionNormalize";
import TransformerFunctionNot from "./TransformerFunctionNot";
import TransformerFunctionNumberFormat from "./TransformerFunctionNumberFormat";
import TransformerFunctionNumberParse from "./TransformerFunctionNumberParse";
import TransformerFunctionObject from "./TransformerFunctionObject";
import TransformerFunctionOr from "./TransformerFunctionOr";
import TransformerFunctionPad from "./TransformerFunctionPad";
import TransformerFunctionPartition from "./TransformerFunctionPartition";
import TransformerFunctionRange from "./TransformerFunctionRange";
import TransformerFunctionRaw from "./TransformerFunctionRaw";
import TransformerFunctionReduce from "./TransformerFunctionReduce";
import TransformerFunctionReplace from "./TransformerFunctionReplace";
import TransformerFunctionReverse from "./TransformerFunctionReverse";
import TransformerFunctionSlice from "./TransformerFunctionSlice";
import TransformerFunctionSort from "./TransformerFunctionSort";
import TransformerFunctionSplit from "./TransformerFunctionSplit";
import TransformerFunctionString from "./TransformerFunctionString";
import TransformerFunctionSubstring from "./TransformerFunctionSubstring";
import TransformerFunctionSum from "./TransformerFunctionSum";
import TransformerFunctionSwitch from "./TransformerFunctionSwitch";
import TransformerFunctionTemplate from "./TransformerFunctionTemplate";
import TransformerFunctionTest from "./TransformerFunctionTest";
import TransformerFunctionTransform from "./TransformerFunctionTransform";
import TransformerFunctionTrim from "./TransformerFunctionTrim";
import TransformerFunctionUnflatten from "./TransformerFunctionUnflatten";
import TransformerFunctionUpper from "./TransformerFunctionUpper";
import TransformerFunctionUriParse from "./TransformerFunctionUriParse";
import TransformerFunctionUrlDecode from "./TransformerFunctionUrlDecode";
import TransformerFunctionUrlEncode from "./TransformerFunctionUrlEncode";
import TransformerFunctionUuid from "./TransformerFunctionUuid";
import TransformerFunctionValue from "./TransformerFunctionValue";
import TransformerFunctionWrap from "./TransformerFunctionWrap";
import TransformerFunctionXml from "./TransformerFunctionXml";
import TransformerFunctionXmlParse from "./TransformerFunctionXmlParse";
import TransformerFunctionXor from "./TransformerFunctionXor";
import TransformerFunctionYaml from "./TransformerFunctionYaml";
import TransformerFunctionYamlParse from "./TransformerFunctionYamlParse";

export default {
  and: new TransformerFunctionAnd(),
  at: new TransformerFunctionAt(),
  avg: new TransformerFunctionAvg(),
  base64: new TransformerFunctionBase64(),
  boolean: new TransformerFunctionBoolean(),
  coalesce: new TransformerFunctionCoalesce(),
  concat: new TransformerFunctionConcat(),
  contains: new TransformerFunctionContains(),
  csv: new TransformerFunctionCsv(),
  csvparse: new TransformerFunctionCsvParse(),
  date: new TransformerFunctionDate(),
  decimal: new TransformerFunctionDecimal(),
  digest: new TransformerFunctionDigest(),
  distinct: new TransformerFunctionDistinct(),
  entries: new TransformerFunctionEntries(),
  eval: new TransformerFunctionEval(),
  filter: new TransformerFunctionFilter(),
  find: new TransformerFunctionFind(),
  first: new TransformerFunctionCoalesce(), // * alias for coalesce
  flat: new TransformerFunctionFlat(),
  flatten: new TransformerFunctionFlatten(),
  form: new TransformerFunctionForm(),
  formparse: new TransformerFunctionFormParse(),
  group: new TransformerFunctionGroup(),
  if: new TransformerFunctionIf(),
  is: new TransformerFunctionIs(),
  isnull: new TransformerFunctionIsNull(),
  join: new TransformerFunctionJoin(),
  jsonparse: new TransformerFunctionJsonParse(),
  jsonpatch: new TransformerFunctionJsonPatch(),
  jsonpath: new TransformerFunctionJsonPath(),
  jsonpointer: new TransformerFunctionJsonPointer(),
  jwtparse: new TransformerFunctionJwtParse(),
  length: new TransformerFunctionLength(),
  long: new TransformerFunctionLong(),
  lookup: new TransformerFunctionLookup(),
  lower: new TransformerFunctionLower(),
  map: new TransformerFunctionMap(),
  match: new TransformerFunctionMatch(),
  matchall: new TransformerFunctionMatchAll(),
  math: new TransformerFunctionMath(),
  max: new TransformerFunctionMax(),
  merge: new TransformerFunctionMerge(),
  min: new TransformerFunctionMin(),
  normalize: new TransformerFunctionNormalize(),
  not: new TransformerFunctionNot(),
  numberformat: new TransformerFunctionNumberFormat(),
  numberparse: new TransformerFunctionNumberParse(),
  object: new TransformerFunctionObject(),
  or: new TransformerFunctionOr(),
  pad: new TransformerFunctionPad(),
  partition: new TransformerFunctionPartition(),
  range: new TransformerFunctionRange(),
  raw: new TransformerFunctionRaw(),
  reduce: new TransformerFunctionReduce(),
  replace: new TransformerFunctionReplace(),
  reverse: new TransformerFunctionReverse(),
  slice: new TransformerFunctionSlice(),
  sort: new TransformerFunctionSort(),
  split: new TransformerFunctionSplit(),
  string: new TransformerFunctionString(),
  substring: new TransformerFunctionSubstring(),
  sum: new TransformerFunctionSum(),
  switch: new TransformerFunctionSwitch(),
  template: new TransformerFunctionTemplate(),
  test: new TransformerFunctionTest(),
  transform: new TransformerFunctionTransform(),
  trim: new TransformerFunctionTrim(),
  unflatten: new TransformerFunctionUnflatten(),
  upper: new TransformerFunctionUpper(),
  uriparse: new TransformerFunctionUriParse(),
  urldecode: new TransformerFunctionUrlDecode(),
  urlencode: new TransformerFunctionUrlEncode(),
  uuid: new TransformerFunctionUuid(),
  value: new TransformerFunctionValue(),
  wrap: new TransformerFunctionWrap(),
  xml: new TransformerFunctionXml(),
  xmlparse: new TransformerFunctionXmlParse(),
  xor: new TransformerFunctionXor(),
  yaml: new TransformerFunctionYaml(),
  yamlparse: new TransformerFunctionYamlParse(),
};
