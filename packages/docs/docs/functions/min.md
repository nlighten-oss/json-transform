# $$min

Returns the min of all values in the array

### Usage
```transformers
{
  "$$min": [ /* values */ ],
  "by": /* Transformer */,
  "type": /* type of values (to compare by) */,
  "default": /* 0 */
}
```
```transformers
"$$min([default],[type],[by]):{input}"
```
### Returns
`BigDecimal`

### Arguments
| Argument  | Type                     | Values                                 | Required / Default&nbsp;Value | Description                                                                                  |
|-----------|--------------------------|----------------------------------------|-------------------------------|----------------------------------------------------------------------------------------------|
| Primary   | `array`                  |                                        | Yes                           | Array to sum                                                                                 |
| `default` | `BigDecimal`             |                                        | `0`                           | The default value to use for empty values                                                    |
| `by`      | Transformer(`##current`) | Input is `##current` (current element) | `"##current"`                 | A transformer to extract a property to sum by (using ##current to refer to the current item) |
| `type`    | `Enum`                   | `auto`/`string`/`number`/`boolean`     | `auto`                        | Type of values to expect when ordering the input array                                       |

## Examples


```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**

```json
[4, -2, 13.45, null]
```
```transformers
"$$min($$long:-4):$"
```
```json
-4
```

```json
[4, -2, 13.45, null]
```
```transformers
"$$min(-8,NUMBER):$"
```
```json
"-8"
```

```json
[4, -2, 13.45, null]
```
```transformers
"$$min:$"
```
```json
null
```

```json
[4, -2, 13.45, null]
```
```transformers
"$$min(z,STRING):$"
```
```json
-2
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
  "$$min": "$",
  "by": "##current.value"
}
```
```json
null
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
  "$$min": "$",
  "by": "##current.value",
  "default": 1
}
```
```json
1
```


```mdx-code-block
</div>
```