# $$find

Find the first element in a specified array that satisfy the predicate transformer.

### Usage
```transformers
{
  "$$find": [ /* values */ ],
  "by": /* Transformer */
}
```
### Returns
_Same as found element_

:::info
`by` should resolve to a `boolean` value, it uses the [truthy logic](../truthy-logic.md)
:::
### Arguments
| Argument | Type          | Values                                  | Required / Default&nbsp;Value | Description                            |
|----------|---------------|-----------------------------------------|-------------------------------|----------------------------------------|
| Primary  | `array`       |                                         | Yes                           | Array of elements                      |
| `by`     | Transformer(`##current`) | Input is `##current` (current element) | Yes                           | A predicate transformer for an element |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
[
  "",
  0,
  null,
  "1",
  "0"
]
```
```transformers
{ 
  "$$find": "$", 
  "by": "##current" 
}
```
```json
"1"
```

```json
[
  { "a": 0 }, 
  { "a": 1 }
]
```
```transformers
{
  "$$find": "$", 
  "by": "##current.a" 
}
```
```json
{ "a": 1 }
```
```mdx-code-block
</div>
```
