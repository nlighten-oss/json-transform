# $$sum

Returns a sum of all values in the array

### Usage
```transformers
{
  "$$sum": [ /* values */ ],
  "by": /* Transformer */
  "default": /* 0 */
}
```
```transformers
"$$sum([default],[by]):{input}"
```
### Returns
`BigDecimal`

### Arguments
| Argument  | Type                     | Values                                 | Required / Default&nbsp;Value | Description                                                                                  |
|-----------|--------------------------|----------------------------------------|-------------------------------|----------------------------------------------------------------------------------------------|
| Primary   | `array`                  |                                        | Yes                           | Array to sum                                                                                 |
| `default` | `BigDecimal`             |                                        | `0`                           | The default value to use for empty values                                                    |
| `by`      | Transformer(`##current`) | Input is `##current` (current element) | `"##current"`                 | A transformer to extract a property to sum by (using ##current to refer to the current item) |

## Examples


```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**

```json
[4, 2, 13.45, null]
```
```transformers
"$$sum:$"
```
```json
19.45
```

```json
[4, 2, 13.45, null]
```
```transformers
"$$sum(1):$"
```
```json
20.45
```

```json
[
  { "value": 4 },
  { "value": 2 },
  { "value": 13.45 },
  { "value": null }
]
```
```transformers
{
  "$$sum": "$",
  "by": "##current.value"
}
```
```json
19.45
```

```json
[
  { "value": 4 }, 
  { "value": 2 }, 
  { "value": 13.45 }, 
  { "value": null }
]
```
```transformers
{
  "$$sum": "$",
  "by": "##current.value",
  "default": 1
}
```
```json
20.45
```


```mdx-code-block
</div>
```