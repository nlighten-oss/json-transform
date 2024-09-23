# $$yaml

Converts an object to YAML format

:::info
Structure of output may very depending on platform.
:::

### Usage
```transformers
{ 
  "$$yaml": /* value */ 
}
```
```transformers
"$$yaml:{input}"
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
```transformers
{ 
  "$$yaml": "$" 
}
```
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


```mdx-code-block
</div>
```