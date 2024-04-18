# $$jsonpointer

Apply mutations on object paths using JSON Pointer (defined by RFC-6901)

### Usage
```transformers
"$$jsonpointer(<op>,<pointer>,[value]):{input}"
```
```transformers
{
  "$$jsonpointer": /* value */,
  "op": /* GET / SET / REMOVE */,
  "pointer": /* JSON Pointer string */,
  "value": /* value for SET */
}
```
### Returns
Depends on the expression

### Arguments
| Argument  | Type     | Values                              | Required / Default&nbsp;Value | Description                            |
|-----------|----------|-------------------------------------|-------------------------------|----------------------------------------|
| Primary   | `any`    |                                     | Yes                           | Object to query                        |
| `op`      | `Enum`   | `GET` / `SET` / `REMOVE`            | Yes                           | Operation                              |
| `pointer` | `string` | JSON Pointer as defined by RFC-6901 | Yes                           | JSON Pointer to apply operation on     |
| `value`   | `any`    |                                     |                               | Value to use (when operation is `SET`) |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json noInline
{
  "b": [
    "c",
    "d",
    {
      "e": "Hello"
    }
  ]
}
```
```transformers
"$$jsonpointer(GET,/b/2/e):$"
```
```json
"Hello"
```

```json noInline
{
  "b": [
    "c",
    "d",
    {
      "e": "Hello"
    }
  ]
}
```
```transformers
"$$jsonpointer(SET,/b,'$.b[2]'):$"
```
```json
{
  "b": {
    "e": "Hello"
  }
}
```

```json noInline
{
  "b": [
    "c",
    "d",
    {
      "e": "Hello"
    }
  ]
}
```
```transformers
"$$jsonpointer(REMOVE,/b/2):$"
```
```json
{
  "b": [
    "c",
    "d"
  ]
}
```

```json noInline
{
  "pointer": "/pointer"
}
```
```transformers
"$$jsonpointer(GET,$.pointer):$"
```
```json
"/pointer"
```
```mdx-code-block
</div>
```
