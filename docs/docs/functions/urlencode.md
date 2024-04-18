# $$urlencode

URL encodes as string

### Usage
```transformers
"$$urlencode:{input}"
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
"not url+safe?=x&b=path/path"
```
```transformers
"$$urlencode:$"
```
```json
"not+url%2Bsafe%3F%3Dx%26b%3Dpath%2Fpath"
```

```mdx-code-block
</div>
```
