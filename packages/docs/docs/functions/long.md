# $$long

Converts to integer, all decimal digits are truncated

### Usage
```transformers
"$$long:{input}"
```
### Returns
`integer`

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
"123"
```
```transformers
"$$long:$"
```
```json
123
```

```json
"123.5"
```
```transformers
"$$long:$"
```
```json
123
```

```mdx-code-block
</div>
```
