# $$jsonparse

Parses input as JSON string

### Usage
```transformers
"$$jsonparse:{input}"
```
### Returns
`object`

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json noInline
"true"
```
```transformers
"$$jsonparse:$"
```
```json
true
```

```json noInline
"{\"a\":true}"
```
```transformers
"$$jsonparse:$"
```
```json
{ "a": true }
```

```json noInline
"[\"a\",\"b\"]"
```
```transformers
"$$jsonparse:$"
```
```json
["a", "b"]
```
```mdx-code-block
</div>
```
