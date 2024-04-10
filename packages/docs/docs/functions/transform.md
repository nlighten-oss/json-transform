# $$transform

Applies a transformation inside a transformer (Useful for piping functions results).

### Usage
```transformers
{
  "$$transform": /* value */,
  "to": /* Transformer */
}
```
### Returns
`to`'s result type

### Arguments
| Argument | Type                     | Values                               | Required / Default&nbsp;Value | Description                   |
|----------|--------------------------|--------------------------------------|-------------------------------|-------------------------------|
| Primary  |                          |                                      | Yes                           | Input value                   |
| `to`     | Transformer(`##current`) | Input is `##current` (input element) | Yes                           | Transformer to apply on input |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
"a,b"
```
```transformers
{ 
  "$$transform": "$$split(','):$", 
  "to": {
    "left": "##current[0]",
    "right": "##current[1]"
  } 
}
```
```json
{
  "left": "a",
  "right": "b"
}
```
```mdx-code-block
</div>
```
