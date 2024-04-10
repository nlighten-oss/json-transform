# $$form

Converts an object to Form URL-Encoded string (a.k.a Query String)

- Array values will be treated as multiple values for the same key (so the key will be duplicated in the result for each of the values)

### Usage
```transformers
{ 
  "$$form": { /* key-values */ } 
}
```
```transformers
"$$form:{input}"
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
  "b": "B",
  "c": "true"
}
```
```transformers
{ 
  "$$form": "$" 
}
```
```json
"a=1&b=B&c=true"
```

```json
{
  "a": [1, 2],
  "c": true
}
```
```transformers
{ 
  "$$form": "$" 
}
```
```json
"a=1&a=2&c=true"
```


```json
{
  "a": "1",
  "b": "B",
  "c": "true"
}
```
```transformers
"$$form:$"
```
```json
"a=1&b=B&c=true"
```

```json
{
  "a": [1, 2],
  "c": true
}
```
```transformers
"$$form:$"
```
```json
"a=1&a=2&c=true"
```

```mdx-code-block
</div>
```