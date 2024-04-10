# $$coalesce / $$first

Returns the first non-null value.

:::tip
Coalesce can also be referred to as `$$first` instead of `$$coealesce`
:::

### Usage
```transformers
{
  "$$coalesce": [ /* values */ ]
}
```
```transformers
"$$coalesce:{input}"
```
### Returns
_Same as first non-null value_
### Arguments
| Argument | Type    | Values | Required / Default&nbsp;Value | Description                           |
|----------|---------|--------|-------------------------------|---------------------------------------|
| Primary  | `array` |        | Yes                           | Array of elements (may include nulls) |

## Examples

```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**

```json
[null, null, 1, null, 2]
```
```transformers
{ 
  "$$coalesce": "$" 
}
```
```json
1
```

```json
null
```
```transformers
{ 
  "$$coalesce": [
    "$", 
    "default"
  ] 
}
```
```json
"default"
```

```mdx-code-block
</div>
```
