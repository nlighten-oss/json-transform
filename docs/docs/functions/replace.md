# $$replace

Search and replaces a substring in the given input.

### Usage
```transformers
"$$replace(<find>,<replacement>,[type]):{input}"
```
### Returns
`string`
### Arguments
| Argument      | Type     | Values                                 | Required / Default&nbsp;Value | Description                                                                                                                                                                  |
|---------------|----------|----------------------------------------|-------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `find`        | `string` |                                        | Yes                           | Value to search in input string (depends on `type`, if set to REGEX, should be a regular expression)                                                                         |
| `replacement` | `string` |                                        | Yes                           | Value to replace each match (or only first if `type = REGEX-FIRST`), when using regular expression can use group matches (e.g. $1) (Note: to escape `$` if starting with it) |
| `type`        | `Enum`   | `STRING`/`FIRST`/`REGEX`/`REGEX-FIRST` | `STRING`                      | Matching type                                                                                                                                                                |

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
"$$replace(l,x):$"
```
```json
"hexxo"
```

```json
"hello"
```
```transformers
"$$replace([le],x,REGEX):$"
```
```json
"hxxxo"
```

```json
"hello"
```
```transformers
"$$replace([le],x,REGEX-FIRST):$"
```
```json
"hxllo"
```

```json
"hello"
```
```transformers
"$$replace('(ll)',$1i,REGEX):$"
```
```json
"hellio"
```
```mdx-code-block
</div>
```
