# $$reverse

Reverses the order of elements in an array.

### Usage
```transformers
{
  "$$reverse": [ /* values */ ]
}
```
### Returns
`array` (same as input)

### Arguments
| Argument | Type    | Values | Required / Default&nbsp;Value | Description       |
|----------|---------|--------|-------------------------------|-------------------|
| Primary  | `array` |        | Yes                           | Array of elements |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
["a","b","b","c"]
```
```transformers
{ 
  "$$reverse":"$" 
}
```
```json
["c","b","b","a"]
```
```mdx-code-block
</div>
```
