# $$boolean

Evaluates input to boolean using the [Truthy logic](../truthy-logic.md)

Strings evaluation depends on `style` argument:
- By default, value must be `"true"` for `true`.
- Unless `style` is set to `JS`, then any non-empty value is `true`. Arrays and objects of size 0 returns `false`.

### Usage
```transformers
{
  "$$boolean": /* value */,
  "style": "JAVA"
}
```
```transformers
"$$boolean([style]):{input}"
```
### Returns
`boolean`
### Arguments
| Argument | Type   | Values      | Required / Default&nbsp;Value | Description                                                                                     |
|----------|--------|-------------|-------------------------------|-------------------------------------------------------------------------------------------------|
| `style`  | `Enum` | `JAVA`/`JS` | `JAVA`                        | Style of considering truthy values (JS only relates to string handling; not objects and arrays) |

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
"$$boolean:$"
```
```json
true
```

```json
"true"
```
```transformers
"$$boolean:$"
```
```json
true
```

```json
"yes"
```
```transformers
"$$boolean:$"
```
```json
false
```

```json
"yes"
```
```transformers
"$$boolean(JS):$"
```
```json
true
```

```mdx-code-block
</div>
```