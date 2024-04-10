# $$if

Conditionally returns a value, if the evaluation of the condition argument is truthy (using the [Truthy logic](../truthy-logic.md)). A fallback value (if condition evaluates to false) is optional.

### Usage
```transformers
{
  "$$if": /* condition */,
  "then": /* then value */,
  "else": /* else value */
}
```
### Returns
Value of `then` or `else`
### Arguments
| Argument | Type    | Values | Required / Default&nbsp;Value | Description                           |
|----------|---------|--------|-------------------------------|---------------------------------------|
| Primary  | `array` |        | Yes                           | Condition                             |
| `then`   |         |        | Yes                           | Value to return if condition is true  |
| `else`   |         |        | `null`                        | Value to return if condition is false |

## Alternative form

```transformers
{
  "$$if": [ /* condition */, /* then */, /* else ? */]
}
```
### Arguments
| Argument | Type    | Values | Required / Default&nbsp;Value | Description         |
|----------|---------|--------|-------------------------------|---------------------|
| Primary  | `array` |        | Yes                           | Array of size 2 / 3 |


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
{ "$$if": true, "then": "b", "else": "c" }
```
```json
"b"
```


```json

```
```transformers
{ "$$if": [true, "b"] }
```
```json
"b"
```


```json

```
```transformers
{ "$$if": false, "then": "b" }
```
```json
null
```


```json

```
```transformers
{ "$$if": [null, "b"] }
```
```json
null
```


```json
{
  "a": true, 
  "b": true
}
```
```transformers
{ 
  "$$if": [
    "$.a", 
    { 
      "$$if": [
        "$.b", 
        "a&b" 
      ] 
    } 
  ]
}
```
```json
"a&b"
```


```json
{ 
  "a": false,
  "b": true
}
```
```transformers
{ 
  "$$if": [
    "$.a", 
    "a|b", 
    { 
      "$$if": [
        "$.b",
        "a|b" 
      ] 
    }
  ] 
}
```
```json
"a|b"
```

```mdx-code-block
</div>
```
