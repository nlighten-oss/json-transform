# $$not

Returns the opposite of the input's boolean evaluated value (this returns the opposite of [$$boolean](boolean))

### Usage
```transformers
"$$not([style]):{input}"
```
### Returns
`boolean`
### Arguments
| Argument | Type   | Values      | Required / Default&nbsp;Value | Description                        |
|----------|--------|-------------|-------------------------------|------------------------------------|
| `style`  | `Enum` | `JAVA`/`JS` | `JAVA`                        | Style of considering truthy values |

## Examples

```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**

```json
5
```
```transformers
"$$not:$"
```
```json
false
```

```json
"true"
```
```transformers
"$$not:$"
```
```json
false
```

```json
"yes"
```
```transformers
"$$not:$"
```
```json
true
```

```json
"yes"
```
```transformers
"$$not(JS):$"
```
```json
false
```
```mdx-code-block
</div>
```
