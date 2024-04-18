# $$avg

Returns the average of all values in the array

### Usage
```transformers
{
  "$$avg": [ /* values */ ],
  "by": /* Transformer */
  "default": /* 0 */
}
```
```transformers
"$$avg([default],[by]):{input}"
```
### Returns
`BigDecimal`

### Arguments
| Argument  | Type                     | Values                                 | Required / Default&nbsp;Value | Description                                                                                  |
|-----------|--------------------------|----------------------------------------|-------------------------------|----------------------------------------------------------------------------------------------|
| Primary   | `array`                  |                                        | Yes                           | Array to average                                                                             |
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
"$$avg:$"
```
```json
4.86
```

```json
[4, 2, 13.45, null]
```
```transformers
"$$avg(1):$"
```
```json
5.11
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
  "$$avg": "$",
  "by": "##current.value"
}
```
```json
4.86
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
  "$$avg": "$",
  "by": "##current.value",
  "default": 1
}
```
```json
5.11
```


```mdx-code-block
</div>
```