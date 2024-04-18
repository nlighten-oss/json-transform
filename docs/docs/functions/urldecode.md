# $$urldecode

URL decodes as string

### Usage
```transformers
"$$urldecode:{input}"
```
### Returns
`string`

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
"not+url%2Bsafe%3F%3Dx%26b%3Dpath%2Fpath"
```
```transformers
"$$urldecode:$"
```
```json
"not url+safe?=x&b=path/path"
```

```mdx-code-block
</div>
```
