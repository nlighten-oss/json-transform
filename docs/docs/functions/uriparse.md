# $$uriparse

Parses a URI to its components

### Usage
```transformers
"$$uriparse:{input}"
```
### Returns
`object` (See examples below) / `null` (if not a valid URI)

### Arguments
| Argument       | Type     | Values | Required / Default&nbsp;Value | Description            |
|----------------|----------|--------|-------------------------------|------------------------|
| Primary        | `string` |        | Yes                           | A URI formatted string |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
"https://user:pass@example.com:9090/whatever/?q=a&q=b#fragment"
```
```transformers
"$$uriparse:$"
```
```json
{
  "scheme": "https",
  "user_info": "user:pass",
  "authority": "user:pass@example.com:9090",
  "host": "example.com:9090",
  "hostname": "example.com",
  "port": 9090,
  "path": "/whatever/",
  "query": {
    "q": "a",
    "q$$": [ "a", "b" ]
  },
  "query_raw": "q=a&q=b",
  "fragment": "fragment"
}
```

```json
"https://example.com/whatever"
```
```transformers
"$$uriparse:$"
```
```json
{
  "scheme": "https",
  "authority": "example.com",
  "host": "example.com",
  "hostname": "example.com",
  "path": "/whatever"
}
```

```mdx-code-block
</div>
```
