# $$object

Reduces an array of entries into an object

:::note
*Entry is in the form of `[ key, value ]`
:::

### Usage
```transformers
{
  "$$object": [ /* entries */ ]
}
```
```transformers
"$$object:{input}"
```
### Returns
`object`

### Arguments
| Argument | Type    | Values | Required / Default&nbsp;Value | Description                                      |
|----------|---------|--------|-------------------------------|--------------------------------------------------|
| Primary  | `array` |        | Yes                           | Array of entries (in the form of `[key, value]`) |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
[
  ["a", 1],
  ["b", true],
  ["c", "C"]
]
```
```transformers
{ "$$object": "$" }
```
```json
{
  "a": 1,
  "b": true,
  "c": "C"
}
```

```json
[
  [0, 1],
  [1, true],
  [2, "C"]
]
```
```transformers
{ "$$object": "$" }
```
```json
{
  "0": 1,
  "1": true,
  "2": "C"
}
```

```json
[
  ["a", 1],
  ["b", true],
  ["c", "C"]
]
```
```transformers
"$$object:$"
```
```json
{
  "a": 1,
  "b": true,
  "c": "C"
}
```

```json
[
  [0, 1],
  [1, true],
  [2, "C"]
]
```
```transformers
"$$object:$"
```
```json
{
  "0": 1,
  "1": true,
  "2": "C"
}
```

```mdx-code-block
</div>
```

