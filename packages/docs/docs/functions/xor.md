# $$xor

Evaluates to `true` if only one of the values provided will evaluate to `true` (using the [Truthy logic](../truthy-logic.md)).

### Usage
```transformers
{
  "$$xor": [ /* values */ ]
}
```
### Returns
`boolean`

### Arguments
| Argument | Type            | Values | Required / Default&nbsp;Value | Description       |
|----------|-----------------|--------|-------------------------------|-------------------|
| Primary  | `array`         |        | Yes                           | Values to check   |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
```
```transformers
{
  "$$xor": [
    true,
    true,
    false
  ]
}
```
```json
false
```

```json
```
```transformers
{
  "$$xor": [
    true,
    false,
    false
  ]
}
```
```json
true
```

```mdx-code-block
</div>
```
