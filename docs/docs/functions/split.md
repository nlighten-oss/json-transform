# $$split

Splits a string using a given delimiter/regex

### Usage
```transformers
"$$split(<delimiter>,[limit]):{input}"
```
### Returns
`string[]`
### Arguments
| Argument    | Type      | Values | Required / Default&nbsp;Value | Description                                                                             |
|-------------|-----------|--------|-------------------------------|-----------------------------------------------------------------------------------------|
| `delimiter` | `string`  |        | Yes                           | Delimiter to split by (can be a regular expression)                                     |
| `limit`     | `integer` |        | `0` (No limit)                | Limit the amount of elements returned (and by that, the amount the pattern get matched) |

## Examples

```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
"a,b,c"
```
```transformers
"$$split(','):$"
```
```json
["a","b","c"]
```


```json
"hello world"
```
```transformers
"$$split(ll?):$"
```
```json
["he","o"," wor","d"]
```


```json
"hello world"
```
```transformers
"$$split(o,2):$"
```
```json
["hell"," world"]
```


```mdx-code-block
</div>
```
