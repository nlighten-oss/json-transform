# $$filter

Filter input array to all the elements that satisfy the predicate transformer.

### Usage
```transformers
{
  "$$filter": [ /* values */ ],
  "by": /* Transformer */
}
```
### Returns
`array` (same items type as input)

:::info
`by` should resolve to a `boolean` value, it uses the [truthy logic](../truthy-logic.md)
:::
### Arguments
| Argument | Type          | Values                                 | Required / Default&nbsp;Value | Description                            |
|----------|---------------|----------------------------------------|-------------------------------|----------------------------------------|
| Primary  | `array`       |                                        | Yes                           | Array of elements                      |
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
  "a",
  null,
  "b",
  "",
  false,
  true
]
```
```transformers
{ 
  "$$filter": "$", 
  "by": "##current" 
}
```
```json
[
  "a",
  "b",
  true
]
```
```mdx-code-block
</div>
```
