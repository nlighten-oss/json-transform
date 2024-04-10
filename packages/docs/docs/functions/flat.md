# $$flat

Flatten an array of arrays (non array elements will remain).

:::note
All `null` elements are removed from result
:::

### Usage
```transformers
{
  "$$flat": [ /* arrays / elements */ ]
}
```
### Returns
`array`
### Arguments
| Argument | Type    | Values | Required / Default&nbsp;Value | Description                |
|----------|---------|--------|-------------------------------|----------------------------|
| Primary  | `array` |        | Yes                           | Array of arrays / elements |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
[
  "a",
  "b"
]
```
```transformers
{
  "$$flat": [
    "$", 
    null, 
    "c", 
    ["d", null]
  ] 
}
```
```json
[
  "a",
  "b",
  "c",
  "d"
]
```

```mdx-code-block
</div>
```
