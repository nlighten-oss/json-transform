# $$or


Evaluates to `true` if any of the values provided will evaluate to `true` (using the [Truthy logic](../truthy-logic.md)).

### Usage
```transformers
{
  "$$or": [ /* values */ ]
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
[1,3]
```
```transformers
{
  "$$or": [
    { "$$is": "$[0]", "eq": 2 },
    { "$$is": "$[1]", "eq": 3 }
  ]
}
```
```json
true
```

```json
[1,4]
```
```transformers
{
  "$$or": [
    { "$$is": "$[0]", "eq": 2 },
    { "$$is": "$[1]", "eq": 3 }
  ]
}
```
```json
false
```

```json
{ "a": null, "b": null }
```
```transformers
{
  "$$or": [ "$.a", "$.b" ]
}
```
```json
false
```

```mdx-code-block
</div>
```
