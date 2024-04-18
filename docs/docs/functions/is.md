# $$is

Returns `true` if all predicates arguments are satisfied.

### Usage
```transformers
{
  "$$is": /* value to check */,
  "in": [ /* possible values */ ],
  "nin": [ /* forbidden values */ ],
  "eq": /* value to equal */,
  "neq": /* value to not equal */,
  "gt": /* value to be greater than */,
  "gte": /* value to be greater than or equal */,
  "lt": /* value to be lower than */,
  "lte": /* value to be lower than or equal */,
}
```
### Returns
`boolean`

### Arguments
| Argument | Type            | Values | Required / Default&nbsp;Value | Description                                                        |
|----------|-----------------|--------|-------------------------------|--------------------------------------------------------------------|
| Primary  |                 |        | Yes                           | Value to check against                                             |
| `in`     | `array`         |        |                               | Array of values the input should be part of                        |
| `nin`    | `array`         |        |                               | Array of values the input should **NOT** be part of                |
| `eq`     | (same as input) |        |                               | A Value the input should be equal to                               |
| `neq`    |                 |        |                               | A Value the input should **NOT** be equal to                       |
| `gt`     | (same as input) |        |                               | A Value the input should be greater than (input > value)           |
| `gte`    | (same as input) |        |                               | A Value the input should be greater than or equal (input >= value) |
| `lt`     | (same as input) |        |                               | A Value the input should be lower than (input < value)             |
| `lte`    | (same as input) |        |                               | A Value the input should be lower than or equal (input \<= value)  |

* `gt`/`gte`/`lt`/`lte` - Uses the [comparison logic](../comparison-logic.md)

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
"TEST"
```
```transformers
{ "$$is": "$", "eq": "TEST" }
```
```json
true
```


```json
"TEST"
```
```transformers
{ "$$is": "$", "neq": "TEST" }
```
```json
false
```


```json
"DEV"
```
```transformers
{ "$$is": "$", "in": ["DEV","PROD"] }
```
```json
true
```


```json
"DEV"
```
```transformers
{ "$$is": "$", "nin": ["DEV","TEST"] }
```
```json
false
```


```json
2
```
```transformers
{ "$$is": "$", "gt": 1, "lt": 3 }
```
```json
true
```


```json
1
```
```transformers
{ "$$is": "$", "gt": 1, "lt": 3 }
```
```json
false
```


```json
3
```
```transformers
{ "$$is": "$", "gt": 1, "lt": 3 }
```
```json
false
```


```json
[ { "a": 1 } ]
```
```transformers
{ 
  "$$is": "$", 
  "in":[
    [ {"a": 4} ],
    [ {"a": 1} ],
    [ {"a": 3} ]
  ]
}
```
```json
true
```


```mdx-code-block
</div>
```
