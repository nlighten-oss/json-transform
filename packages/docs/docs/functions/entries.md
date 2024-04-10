# $$entries

Gets the entries* of an object or an array

:::note
*Entry is in the form of `[ key / index, value ]`
:::

### Usage
```transformers
{
  "$$entries": /* value */
}
```
```transformers
"$$entries:{input}"
```
### Returns
`Entry[]` (`Entry` is defined by `[string, any]`)

### Arguments
| Argument | Type             | Values | Required / Default&nbsp;Value | Description                               |
|----------|------------------|--------|-------------------------------|-------------------------------------------|
| Primary  | `object`/`array` |        | Yes                           | An object or an array to get entries from |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
{
  "a": 1,
  "b": true,
  "c": "C"
}
```
```transformers
{
  "$$entries": "$" 
}
```
```json
[
  [ "a", 1 ],
  [ "b", true ],
  [ "c", "C" ]
]
```


```json
[
  1,
  true,
  "C"
]
```
```transformers
{ 
  "$$entries": "$" 
}
```
```json
[
  [ 0, 1 ],
  [ 1, true ],
  [ 2, "C" ]
]
```


```json
{
  "a": 1,
  "b": true,
  "c": "C"
}
```
```transformers
"$$entries:$"
```
```json
[
  ["a", 1],
  ["b", true],
  ["c", "C"]
]
```

```json
[
  1, 
  true, 
  "C"
]
```
```transformers
"$$entries:$"
```
```json
[
  [0, 1],
  [1, true],
  [2, "C"]
]
```

```mdx-code-block
</div>
```