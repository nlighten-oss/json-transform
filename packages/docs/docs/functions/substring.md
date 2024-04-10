# $$substring

Gets a slice of an array by indices (negative begin index will slice from the end)

### Usage
```transformers
"$$slice(<begin>,[end]):{input}"
```
### Returns
`array` (same items type as input)

### Arguments
| Argument | Type      | Values | Required / Default&nbsp;Value | Description                                                                             |
|----------|-----------|--------|-------------------------------|-----------------------------------------------------------------------------------------|
| `begin`  | `integer` |        | Yes                           | Index of first character to slice from (if negative, counts from the end of the string) |
| `end`    | `integer` |        |                               | Index of last character to slice to (if negative, counts from the end of the string)    |

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
"$$substring(1,3):$"
```
```json
"ell"
```

```json
"hello-world"
```
```transformers
"$$substring(-5):$"
```
```json
"world"
```

```mdx-code-block
</div>
```