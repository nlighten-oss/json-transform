# $$value

Returns the value if it passes the [Truthy logic](../truthy-logic.md), or `null` otherwise.

### Usage
```transformers
{
  "$$value": /* value */
}
```
### Returns
_Same type as input_ or `null`

### Arguments
| Argument | Type     | Values | Required / Default&nbsp;Value | Description |
|----------|----------|--------|-------------------------------|-------------|
| Primary  | Anything |        | Yes                           | Any value   |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
"hello"
```
```transformers
{ 
  "$$value": "$" 
}
```
```json
"hello"
```


```json
""
```
```transformers
{ 
  "$$value": "$" 
}
```
```json
null
```


```mdx-code-block
</div>
```

