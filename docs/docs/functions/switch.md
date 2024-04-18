# $$switch

Switch-case expression.
Value is compared to each of the keys in `cases` and a matching **key** will result with its **value**, 
otherwise `default` value or `null` will be returned.

### Usage
```transformers
{
  "$$switch": /* value */,
  "cases": {
    /* "case_value": result_value */
  },
  "default": /* default value */
}
```
### Returns
Same as `default` value or one of the `cases` values

### Arguments
| Argument  | Type     | Values                     | Required / Default&nbsp;Value | Description                                           |
|-----------|----------|----------------------------|-------------------------------|-------------------------------------------------------|
| Primary   | `string` |                            | Yes                           | Value to test                                         |
| `cases`   | `object` | A map of `string` to value | Yes                           |                                                       |
| `default` |          |                            | `null`                        | Fallback value in case no match to any key in `cases` |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
"B"
```
```transformers
{ 
  "$$switch": "$", 
  "cases": {
    "a": 1,
    "B": 2,
    "c": 3
  }
}
```
```json
2
```


```json
"D"
```
```transformers
{
  "$$switch": "$", 
  "cases": {
    "a": 1,
    "B": 2,
    "c": 3
  }
}
```
```json
null
```


```json
"D"
```
```transformers
{ 
  "$$switch": "$", 
  "cases": {
    "a": 1,
    "B": 2,
    "c": 3
  }, 
  "default": 4
}
```
```json
4
```


```json
{
  "a": 1,
  "B": 2,
  "c": 3
}
```
```transformers
{ 
  "$$switch": "B", 
  "cases": "$" 
}
```
```json
2
```


```mdx-code-block
</div>
```
