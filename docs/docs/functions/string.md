# $$string

Converts to string (if `json` set to `true`, will convert null and strings also as json strings)

### Usage
```transformers
"$$string([json]):{input}"
```
### Returns
`string`
### Arguments
| Argument | Type      | Values | Required / Default&nbsp;Value | Description                                                                                               |
|----------|-----------|--------|-------------------------------|-----------------------------------------------------------------------------------------------------------|
| `json`   | `boolean` |        | `false`                       | Whether to convert `null` and strings to json (otherwise, null stays null and strings are returned as-is) |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
123
```
```transformers
"$$string:$"
```
```json
"123"
```

```json
null
```
```transformers
"$$string:$"
```
```json
null
```

```json
null
```
```transformers
"$$string(true):$"
```
```json
"null"
```

```json
"str"
```
```transformers
"$$string:$"
```
```json
"str"
```

```json
"str"
```
```transformers
"$$string(true):$"
```
```json
"\"str\""
```

```json
["a"]
```
```transformers
"$$string:$"
```
```json
"[\"a\"]"
```

```json
{"a":1}
```
```transformers
"$$string:$"
```
```json
"{\"a\":1}"
```
```mdx-code-block
</div>
```
