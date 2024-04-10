# $$reduce

Reduce an array with an initial value (`identity`) and a context transformer to a single value.

### Usage
```transformers
{
  "$$reduce": [ /* values */ ],
  "identity": /* initial value */,
  "to": /* Transformer */
}
```
### Returns
Type of `to`'s result or `identity`'s 

### Arguments
| Argument   | Type                                               | Values                                                                                                           | Required / Default&nbsp;Value | Description                                                                                |
|------------|----------------------------------------------------|------------------------------------------------------------------------------------------------------------------|-------------------------------|--------------------------------------------------------------------------------------------|
| Primary    | `array`                                            |                                                                                                                  | Yes                           | Array of elements                                                                          |
| `to`       | Transformer(`##accumulator`,`##current`,`##index`) | Input is `##accumulator` (previous accumulator value), `##current` (current element) & `##index` (current index) | Yes                           | Transformer to apply on each element (with last accumulation) to get the next accumulation |
| `identity` |                                                    |                                                                                                                  | Yes                           | Initial value to start the accumulation with                                               |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
[
  {
    "amount": 10
  },
  {
    "amount": 20
  }
]
```
```transformers
{ 
  "$$reduce":"$", 
  "to": "$$math(##accumulator,+,##current.amount)", 
  "identity": 1 
}
```
```json
31
```

```mdx-code-block
</div>
```
