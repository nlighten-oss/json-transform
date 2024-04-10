# $$yamlparse

Parses a YAML format to `object`

### Usage
```transformers
{ 
  "$$yamlparse": /* YAML string */ 
}
```
```transformers
"$$yamlparse:{input}"
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

```yaml
a: '1'
b:
- B
- 2
c: true
d:
e:
- E
- '800'

```
```transformers
{ 
  "$$yamlparse": "$" 
}
```
```json
{
  "a": "1",
  "b": [
    "B",
    2
  ],
  "c": true,
  "d": {
    "e": [
      "E",
      "800"
    ]
  }
}
```

```mdx-code-block
</div>
```