# $$raw

Returns the input as-is without interpreting transformers

### Usage
```transformers
{
  "$$raw": /* value */
}
```
### Returns
_Same type as input_

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

```
```transformers
{ 
  "$$raw": "$" 
}
```
```json
"$"
```


```json

```
```transformers
{ 
  "$$raw": "$$boolean:$" 
}
```
```json
"$$boolean:$"
```


```json

```
```transformers
{ 
  "$$raw": { 
    "$$sort": [2,3,1] 
  } 
}
```
```json
{ 
  "$$sort": [2,3,1]
}
```


```mdx-code-block
</div>
```

