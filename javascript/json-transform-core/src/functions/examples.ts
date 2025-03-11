import { EmbeddedTransformerFunction, JsonTransformExample } from "./types";

import _and from "./examples/and.json";
import _at from "./examples/at.json";
import _avg from "./examples/avg.json";
import _base64 from "./examples/base64.json";
import _boolean from "./examples/boolean.json";
import _coalesce from "./examples/coalesce.json";
import _concat from "./examples/concat.json";
import _contains from "./examples/contains.json";
import _csv from "./examples/csv.json";
import _csvparse from "./examples/csvparse.json";
import _date from "./examples/date.json";
import _decimal from "./examples/decimal.json";
import _digest from "./examples/digest.json";
import _distinct from "./examples/distinct.json";
import _entries from "./examples/entries.json";
import _eval from "./examples/eval.json";
import _every from "./examples/every.json";
import _filter from "./examples/filter.json";
import _find from "./examples/find.json";
import _findindex from "./examples/findindex.json";
import _flat from "./examples/flat.json";
import _flatten from "./examples/flatten.json";
import _form from "./examples/form.json";
import _formparse from "./examples/formparse.json";
import _group from "./examples/group.json";
import _if from "./examples/if.json";
import _indexof from "./examples/indexof.json";
import _is from "./examples/is.json";
import _isnull from "./examples/isnull.json";
import _join from "./examples/join.json";
import _jsonparse from "./examples/jsonparse.json";
import _jsonpatch from "./examples/jsonpatch.json";
import _jsonpath from "./examples/jsonpath.json";
import _jsonpointer from "./examples/jsonpointer.json";
import _jwtparse from "./examples/jwtparse.json";
import _length from "./examples/length.json";
import _long from "./examples/long.json";
import _lookup from "./examples/lookup.json";
import _lower from "./examples/lower.json";
import _map from "./examples/map.json";
import _match from "./examples/match.json";
import _matchall from "./examples/matchall.json";
import _math from "./examples/math.json";
import _max from "./examples/max.json";
import _merge from "./examples/merge.json";
import _min from "./examples/min.json";
import _normalize from "./examples/normalize.json";
import _not from "./examples/not.json";
import _numberformat from "./examples/numberformat.json";
import _numberparse from "./examples/numberparse.json";
import _object from "./examples/object.json";
import _or from "./examples/or.json";
import _pad from "./examples/pad.json";
import _partition from "./examples/partition.json";
import _range from "./examples/range.json";
import _raw from "./examples/raw.json";
import _reduce from "./examples/reduce.json";
import _repeat from "./examples/repeat.json";
import _replace from "./examples/replace.json";
import _reverse from "./examples/reverse.json";
import _slice from "./examples/slice.json";
import _some from "./examples/some.json";
import _sort from "./examples/sort.json";
import _split from "./examples/split.json";
import _string from "./examples/string.json";
import _substring from "./examples/substring.json";
import _sum from "./examples/sum.json";
import _switch from "./examples/switch.json";
import _template from "./examples/template.json";
import _test from "./examples/test.json";
import _transform from "./examples/transform.json";
import _trim from "./examples/trim.json";
import _unflatten from "./examples/unflatten.json";
import _upper from "./examples/upper.json";
import _uriparse from "./examples/uriparse.json";
import _urldecode from "./examples/urldecode.json";
import _urlencode from "./examples/urlencode.json";
import _uuid from "./examples/uuid.json";
import _value from "./examples/value.json";
import _wrap from "./examples/wrap.json";
import _xml from "./examples/xml.json";
import _xmlparse from "./examples/xmlparse.json";
import _xor from "./examples/xor.json";
import _yaml from "./examples/yaml.json";
import _yamlparse from "./examples/yamlparse.json";

export default {
  and: _and,
  at: _at,
  avg: _avg,
  base64: _base64,
  boolean: _boolean,
  coalesce: _coalesce,
  concat: _concat,
  contains: _contains,
  csv: _csv,
  csvparse: _csvparse,
  date: _date,
  decimal: _decimal,
  digest: _digest,
  distinct: _distinct,
  entries: _entries,
  eval: _eval,
  every: _every,
  filter: _filter,
  find: _find,
  findindex: _findindex,
  flat: _flat,
  flatten: _flatten,
  form: _form,
  formparse: _formparse,
  group: _group,
  if: _if,
  indexof: _indexof,
  is: _is,
  isnull: _isnull,
  join: _join,
  jsonparse: _jsonparse,
  jsonpatch: _jsonpatch,
  jsonpath: _jsonpath,
  jsonpointer: _jsonpointer,
  jwtparse: _jwtparse,
  length: _length,
  long: _long,
  lookup: _lookup,
  lower: _lower,
  map: _map,
  match: _match,
  matchall: _matchall,
  math: _math,
  max: _max,
  merge: _merge,
  min: _min,
  normalize: _normalize,
  not: _not,
  numberformat: _numberformat,
  numberparse: _numberparse,
  object: _object,
  or: _or,
  pad: _pad,
  partition: _partition,
  range: _range,
  raw: _raw,
  reduce: _reduce,
  repeat: _repeat,
  replace: _replace,
  reverse: _reverse,
  slice: _slice,
  some: _some,
  sort: _sort,
  split: _split,
  string: _string,
  substring: _substring,
  sum: _sum,
  switch: _switch,
  template: _template,
  test: _test,
  transform: _transform,
  trim: _trim,
  unflatten: _unflatten,
  upper: _upper,
  uriparse: _uriparse,
  urldecode: _urldecode,
  urlencode: _urlencode,
  uuid: _uuid,
  value: _value,
  wrap: _wrap,
  xml: _xml,
  xmlparse: _xmlparse,
  xor: _xor,
  yaml: _yaml,
  yamlparse: _yamlparse,
} as Record<EmbeddedTransformerFunction, any> as Record<EmbeddedTransformerFunction, JsonTransformExample>;
