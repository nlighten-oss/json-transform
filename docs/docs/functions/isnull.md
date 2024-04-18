# $$isnull

Returns true if value does not exist or equal to null

### Usage
```transformers
"$$isnull:{input}"
```
### Returns
`boolean`

## Examples

```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**



```json
"something"
```
```transformers
"$$isnull:$"
```
```json
false
```

```json
undefined
```
```transformers
"$$isnull:$"
```
```json
true
```

```json
null
```
```transformers
"$$isnull:$"
```
```json
true
```
```mdx-code-block
</div>
```
