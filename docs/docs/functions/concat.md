# $$concat

Concatenates primary value array with elements or other arrays of elements.  

:::note
Elements which are `null` on the primary value will be ignored.
:::

### Usage
```transformers
{
  "$$concat": [ /* arrays / elements */ ]
}
```
```transformers
"$$concat:{input}"
```
### Returns
`array`
### Arguments
| Argument | Type    | Values | Required / Default&nbsp;Value | Description                                            |
|----------|---------|--------|-------------------------------|--------------------------------------------------------|
| Primary  | `array` |        | Yes                           | Array of arrays / elements (null elements are ignored) |


## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
[["a","b"],["c","d"]]
```
```transformers
{ "$$concat": "$" }
```
```json
["a","b","c","d"]
```


```json
["a","b"]
```
```transformers
{ "$$concat": ["$", ["c", "d"]] }
```
```json
["a","b","c","d"]
```


```json
["a","b"]
```
```transformers
{ "$$concat": ["$", "c", "d"] }
```
```json
["a","b","c","d"]
```


```json
["a","b"]
```
```transformers
{ "$$concat": ["$", null, "c", ["d", null]] }
```
```json
["a","b","c","d",null]
```


```mdx-code-block
</div>
```

