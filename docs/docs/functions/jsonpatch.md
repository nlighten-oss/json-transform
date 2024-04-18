# $$jsonpatch

Apply patches defined by JSON Patch RFC-6902

### Usage
```transformers
{
  "$$jsonpatch": /* value */,
  "ops": [ /* JSON Patch operations */ ]
}
```
```transformers
"$$jsonpatch(<ops>):{input}"
```
### Returns
Depends on the expression

### Arguments
| Argument | Type    | Values                                        | Required / Default&nbsp;Value | Description          |
|----------|---------|-----------------------------------------------|-------------------------------|----------------------|
| Primary  | `any`   |                                               | Yes                           | Object to patch      |
| `ops`    | `array` | Operations as defined in RFC-6902 (see below) | Yes                           | A list of operations |

### JSON Patch Operations

| Operation | Example                                                                                           |
|-----------|---------------------------------------------------------------------------------------------------|
| Add       | `{ "op":"add", "path":"/...", "value":"..." }`                                                    |
| Remove    | `{ "op":"remove", "path":"/..." }`                                                                |
| Replace   | `{ "op":"replace", "path":"/...", "value":"..." }`                                                |
| Move      | `{ "op":"move", "path":"/...", "from":"/..." }`                                                   |
| Copy      | `{ "op":"copy", "path":"/...", "from":"/..." }`                                                   |
| Test      | `{ "op":"test", "path":"/...", "value":"..." }` (if test fails, the function result will be null) |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json noInline
{
  "a": {
    "b": "c"
  }
}
```
```transformers
 {
        "$$jsonpatch": "$",
        "ops": [
            { "op":"add", "path":"/a/d", "value":"e" }
        ]
    }
```
```json
{
  "a": {
    "b": "c",
    "d": "e"
  }
}
```

```mdx-code-block
</div>
```
