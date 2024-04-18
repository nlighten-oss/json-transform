# $$unflatten

Accepts an object with dot separated field names and merges them into an hierarchical object.

### Usage
```transformers
{
  "$$unflatten": /* object */,
  "target": /* target object */,
  "path": ""
}
```
```transformers
"$$unflatten([target],[path]):{input}"
```
### Returns
`array`
### Arguments
| Argument       | Type     | Values | Required / Default&nbsp;Value | Description                               |
|----------------|----------|--------|-------------------------------|-------------------------------------------|
| Primary        | `object` |        | Yes                           | Object with dot separated field names     |
| `target`       | `object` |        | `null`                        | A target to merge into                    |
| `path`         | `string` |        | `null`                        | The root path in the target to merge into |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
{
  "a.a1": 123,
  "a.a2.b.c": true,
  "b": "bbb"
}
```
```transformers
{
  "$$unflatten": "$",
  "path":"x",
  "target": {
    "y": 0
  }
}
```
```json
{
  "y": 0,
  "x": {
    "a": {
      "a1": 123,
      "a2": {
        "b": {
          "c": true
        }
      }
    },
    "b": "bbb"
  }
}
```



```json
{
  "a.a1": 123,
  "a.a2.b.c": true,
  "b": "bbb"
}
```
```transformers
"$$unflatten:$"
```
```json
{
  "a": {
    "a1": 123,
    "a2": {
      "3": {
        "c": true
      }
    }
  },
  "b": "bbb"
}
```

```mdx-code-block
</div>
```
