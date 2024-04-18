# $$pad

Pad a provided string with a certain character(s) repeated until a certain width of output string

(Strings longer than `width` will be returned as-is)

### Usage
```transformers
"$$pad(<direction>,<width>,[character]):{input}"
```
### Returns
`string`
### Arguments
| Argument    | Type      | Values                           | Required / Default&nbsp;Value | Description                                     |
|-------------|-----------|----------------------------------|-------------------------------|-------------------------------------------------|
| `direction` | `Enum`    | `LEFT`(/`START`)/`RIGHT`(/`END`) | Yes                           | On which side of the input to pad               |
| `width`     | `integer` |                                  | Yes                           | What is the maximum length of the output string |
| `character` | `string`  |                                  | `"0"`                         | The character(s) to pad with (0 is the default) |

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
"$$pad(START,5):$"
```
```json
"00123"
```

```json
"TEST"
```
```transformers
"$$pad(END,10,XY):$"
```
```json
"TESTXYXYXY"
```
```mdx-code-block
</div>
```
