# $$and

Evaluates to `true` if all values provided will evaluate to `true` (using the [Truthy logic](../truthy-logic.md)).

### Usage
```transformers
{
  "$$and": [ /* values */ ]
}
```
```transformers
"$$and:{input}"
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
[2,3]
```
```transformers
{
  "$$and": [
    { "$$is": "$[0]", "eq": 2 },
    { "$$is": "$[1]", "eq": 3 }
  ]
}
```
```json
true
```

```json
[2,4]
```
```transformers
{
  "$$and": [
    { "$$is": "$[0]", "eq": 2 },
    { "$$is": "$[1]", "eq": 3 }
  ]
}
```
```json
false
```

```json
[2,4]
```
```transformers
"$$and:$"
```
```json
false
```

```mdx-code-block
</div>
```
