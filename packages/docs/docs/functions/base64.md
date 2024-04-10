# $$base64

Encode to or decode from base64.

### Usage
```transformers
{
  "$$base64": /* value */,
  "action": "ENCODE",
  "rfc": "BASIC",
  "without_padding": false
}
```
```transformers
"$$base64([action],[rfc],[without_padding]):{input}"
```
### Returns
`String`
### Arguments
| Argument          | Type      | Values                | Required / Default&nbsp;Value | Description                                                                                                                                                                                                      |
|-------------------|-----------|-----------------------|-------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Primary           | `String`  |                       | Yes                           | Value to encode/decode                                                                                                                                                                                           |
| `action`          | `Enum`    | `ENCODE`/`DECODE`     | `ENCODE`                      | Whether to encode or decode input                                                                                                                                                                                |
| `rfc`             | `Enum`    | `BASIC`/`URL` /`MIME` | `BASIC`                       | Which alphabet to use (`BASIC` = "The Base64 Alphabet" from RFC-2045, `URL` = "URL and Filename safe Base64 Alphabet" from RFC-4648, `MIME` = Same as `BASIC` but in lines with no more than 76 characters each) |
| `without_padding` | `boolean` | `false`/`true`        | `false`                       | Don't add padding at the end of the output (The character `=`)                                                                                                                                                   |

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
"$$base64(ENCODE):$"
```
```json
"aGVsbG8="
```

```json
"aGVsbG8="
```
```transformers
"$$base64(DECODE):$"
```
```json
"hello"
```

```json
"hello-world"
```
```transformers
{
  "$$base64": "$",
  "action": "ENCODE",
  "rfc": "MIME",
  "without_padding": true
}
```
```json
"aGVsbG8td29ybGRoZWxsby13b3JsZGhlbGxvLXdvcmxkaGVsbG8td29ybGRoZWxsby13b3JsZGhl\nbGxvLXdvcmxkaGVsbG8td29ybGQ"
```



```mdx-code-block
</div>
```