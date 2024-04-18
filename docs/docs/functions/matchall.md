# $$matchall

Returns all matches substring from input by a pattern (and optionally group id)

### Usage
```transformers
"$$matchall(<pattern>,[group]):{input}"
```
### Returns
`string`
### Arguments
| Argument  | Type      | Values | Required / Default&nbsp;Value | Description                                               |
|-----------|-----------|--------|-------------------------------|-----------------------------------------------------------|
| `pattern` | `string`  | Regex  | Yes                           | Regular expression to match and extract from input string |  
| `group`   | `integer` |        | `0`                           | A matching group to return                                |  

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
"$$matchall([el]):$"
```
```json
["e", "l", "l"]
```


```json
"mother father mom dad"
```
```transformers
"$$matchall('m(\\\\w+)',1):$"
```
```json
["other", "om"]
```

```mdx-code-block
</div>
```
