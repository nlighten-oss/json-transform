# $$wrap

Wrap an input string with `prefix` and `suffix`

### Usage
```transformers
"$$wrap([prefix],[suffix]):{input}"
```
### Returns
`string`
### Arguments
| Argument | Type     | Values | Required / Default&nbsp;Value | Description                                     |
|----------|----------|--------|-------------------------------|-------------------------------------------------|
| `prefix` | `string` |        | `""`                          | String that will prefix the input in the output |
| `suffix` | `string` |        | `""`                          | String that will suffix the input in the output |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
"ell"
```
```transformers
"$$wrap(H,o):$"
```
```json
"Hello"
```

```json
"where"
```
```transformers
"$$wrap(,?):$"
```
```json
"where?"
```

```mdx-code-block
</div>
```
