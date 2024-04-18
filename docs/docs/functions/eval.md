# $$eval

Evaluates the input and then transforms the context with the expression

### Usage
```transformers
{
  "$$eval": /* value */
}
```
### Returns
_Same type as input_

### Arguments
| Argument | Type     | Values | Required / Default&nbsp;Value | Description |
|----------|----------|--------|-------------------------------|-------------|
| Primary  | Anything |        | Yes                           | Any value   |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
[
  { "value": 4 },
  { "value": 2 },
  { "value": 13.45 },
  { "value": null }
]
```
```transformers
{
  "$$eval": {
    "$$join":[
      "\\$",
      "$avg:",
      "\\$",
      "..value"
    ]
  }
}
```
```json
4.86
```


```json
[
  { "value": 4 },
  { "value": 2 },
  { "value": 13.45 },
  { "value": null }
]
```
```transformers
{
  "$$eval":{
    "$$jsonparse": {
      "$$join": [
        "{",
        "'$$avg'",
        ":",
        "'$'",
        ", 'by':'##current.value'",
        "}"
      ]
    }
  }
}
```
```json
4.86
```

```mdx-code-block
</div>
```

