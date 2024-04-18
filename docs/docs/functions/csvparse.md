# $$csvparse

Converts a CSV string into an array of objects/arrays

### Usage
```transformers
{
  "$$csvparse": "...",
  "no_headers": false,
  "separator": ",",
  "names": [ /* names */ ]
}
```
```transformers
"$$csvparse([no_headers],[separator],[names]):{input}"
```
### Returns
`string`
### Arguments
| Argument     | Type       | Values         | Required / Default&nbsp;Value       | Description                                                                                                                      |
|--------------|------------|----------------|-------------------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| `no_headers` | `boolean`  | `false`/`true` | `false`                             | Whether to treat the first row as object keys                                                                                    |
| `separator`  | `string`   | `false`/`true` | `","`                               | Use an alternative field separator                                                                                               |
| `names`      | `string[]` |                | (Names taken from the first object) | Names of fields of input arrays (by indices) or objects (can sift if provided less names than there are in the objects provided) |

## Examples

```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**

```csv
a
","
```
```transformers
{
  "$$csvparse": "$"
}
```
```json
[{ "a": "," }]
```

```csv
a
""""
```
```transformers
{
  "$$csvparse": "$"
}
```
```json
[{ "a": "\"" }]
```


```csv
1,2
3,4
```
```transformers
{
  "$$csvparse": "$",
  "no_headers": true
}
```
```json
[
  ["1","2"],
  ["3","4"]
]
```

```csv
a
","
```
```transformers
"$$csvparse:$"
```
```json
[{ "a": "," }]
```

```csv
a
""""
```
```transformers
"$$csvparse:$"
```
```json
[{ "a": "\"" }]
```


```csv
1,2
3,4
```
```transformers
"$$csvparse(true):$"
```
```json
[
  ["1","2"],
  ["3","4"]
]
```

```mdx-code-block
</div>
```