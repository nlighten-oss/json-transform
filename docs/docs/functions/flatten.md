# $$flatten

Flattens a JsonObject into a flat dot seperated list of entries.

### Usage
```transformers
{
  "$$flatten": /* object */,
  "target": /* target object */,
  "prefix": "",
  "array_prefix": "\\$"
}
```
```transformers
"$$flatten([target],[prefix],[array_prefix]):{input}"
```
### Returns
`array`
### Arguments
| Argument       | Type     | Values | Required / Default&nbsp;Value | Description                                                                      |
|----------------|----------|--------|-------------------------------|----------------------------------------------------------------------------------|
| Primary        | `object` |        | Yes                           | Any Object                                                                       |
| `target`       | `object` |        | `null`                        | A target to merge into                                                           |
| `prefix`       | `string` |        | `null`                        | A prefix to add to the base                                                      |
| `array_prefix` | `string` |        | `"$"`                         | Sets how array elements should be prefixed, leave `null` to not flatten arrays   |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
{
  "a1": 123,
  "a2": [ 1, 2 ,3, { "c": true } ]
}
```
```transformers
{
  "$$flatten": {
    "a": "$",
    "b": "bbb"
  }
}
```
```json
{
  "a.a1": 123,
  "a.a2.$0": 1,
  "a.a2.$1": 2,
  "a.a2.$2": 3,
  "a.a2.$3.c": true,
  "b": "bbb"
}
```



```json
{
  "a1": 123,
  "a2": [ 1, 2 ]
}
```
```transformers
"$$flatten('#null',x,''):$"
```
```json
{
  "x.a1": 123,
  "x.a2.0": 1,
  "x.a2.1": 2
}
```

```mdx-code-block
</div>
```
