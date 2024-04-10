# $$jsonpath

Query a JSON document using JSONPath

### Usage
```transformers
"$$jsonpath(<path>,[options]):{input}"
```
```transformers
{
  "$$jsonpath": /* value */,
  "path": /* jsonpath string */,
  "options": [/* configurations string */]
}
```
### Returns
Depends on the expression

### Arguments
| Argument  | Type       | Values                                                                                                         | Required / Default&nbsp;Value | Description                     |
|-----------|------------|----------------------------------------------------------------------------------------------------------------|-------------------------------|---------------------------------|
| Primary   | `any`      |                                                                                                                | Yes                           | Object to query                 |
| `path`    | `string`   |                                                                                                                | Yes                           | JSONPath expression             |
| `options` | `string[]` | A list of options [by jayway](https://github.com/json-path/JsonPath?tab=readme-ov-file#tweaking-configuration) | `[]`                          | Configurations for the resolver |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json noInline
{
  "arr": [
    null,
    "boo"
  ]
}
```
```transformers
"$$jsonpath('\\\\$.arr[1]'):$"
```
```json
"boo"
```

```json noInline
[
  { "id": 1, "active": true },
  { "id": 3, "active": false },
  { "id": 4, "active": true },
  { "id": 5, "active": false }
]
```
```transformers
"$$jsonpath('\\\\$[*][?(@.active == true)]'):$"
```
```json
[
  { "id": 1, "active": true },
  { "id": 4, "active": true }
]
```

```json noInline
{
  "path": "$.path"
}
```
```transformers
"$$jsonpath($.path):$"
```
```json
"$.path"
```
```mdx-code-block
</div>
```
