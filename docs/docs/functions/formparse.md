# $$formparse

Parses a Form URL-Encoded string to `object`

- Every element will have 2 forms in the result object:
  - singular with its original query name (e.g. `q`)
  - plural with its name suffixed with `$$` (e.g. `q$$`)

### Usage
```transformers
{ 
  "$$formparse": "..." 
}
```
```transformers
"$$formparse:{input}"
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

```json
"a=1&b=B&c"
```
```transformers
{ 
  "$$formparse": "$" 
}
```
```json
{
  "a": "1",
  "a$$": ["1"],
  "b": "B",
  "b$$": ["B"],
  "c": "true",
  "c$$": ["true"]
}
```

```json
"a=1&a=2"
```
```transformers
{ 
  "$$formparse": "$" 
}
```
```json
{
  "a": "1",
  "a$$": ["1", "2"]
}
```


```json
"a=1&b=B&c"
```
```transformers
"$$formparse:$"
```
```json
{
  "a": "1",
  "a$$": ["1"],
  "b": "B",
  "b$$": ["B"],
  "c": "true",
  "c$$": ["true"]
}
```

```json
"a=1&a=2"
```
```transformers
"$$formparse:$"
```
```json
{
  "a": "1",
  "a$$": ["1", "2"]
}
```

```mdx-code-block
</div>
```