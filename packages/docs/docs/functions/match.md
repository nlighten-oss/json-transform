# $$match

Returns a matched substring from input by a pattern

### Usage
```transformers
"$$match(<pattern>,[group]):{input}"
```
### Returns
`string`
### Arguments
| Argument  | Type       | Values | Required / Default&nbsp;Value | Description                                               |
|-----------|------------|--------|-------------------------------|-----------------------------------------------------------|
| `pattern` | `string`   | Regex  | Yes                           | Regular expression to match and extract from input string |  
| `group`   | `integer`  |        | `0`                           | A matching group to return                                |

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
"$$match([le]+):$"
```
```json
"ell"
```


```json
"world"
```
```transformers
"$$match('w(\\\\w+)d',1):$"
```
```json
"orl"
```

```mdx-code-block
</div>
```
