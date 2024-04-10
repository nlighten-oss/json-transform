# $$lookup


Joins multiple arrays of objects by a dynamic condition (transformer) on each pair of matches

### Usage
```transformers
{
  "$$lookup": [ /* values */ ],
  "using": [
    { "with": [ /* values 2 */], "as": /* name 2 */, "on": /* Transformer */ },
    { "with": [ /* values 3 */], "as": /* name 3 */, "on": /* Transformer */ },
    ...
  ],
  "to": /* Transformer */
}
```
### Returns
`array` (of type of `to`'s result or the merge of both primary array's item and `with`'s item)
### Arguments
| Argument | Type                                    | Values                                                                            | Required / Default&nbsp;Value                         | Description                                                               |
|----------|-----------------------------------------|-----------------------------------------------------------------------------------|-------------------------------------------------------|---------------------------------------------------------------------------|
| Primary  | `array`                                 | Array of objects                                                                  | Yes                                                   | Array of elements                                                         |
| `using`  | `array`                                 | Array of `UsingEntry`s                                                            | Yes                                                   | Array of defitinitions of how to match other arrays to the main one       |
| `to`     | Transformer(`##current`,`##{as-1}`,...) | Input is `##current` (current element) & `##{as-1}` (matched element from `with`) | Merge `##current` with `##match` (Append & Overwrite) | Transformer to map each pair of elements to its value in the result array |

#### UsingEntity

| Argument | Type                                        | Values                                                                                                                                                                   | Required / Default&nbsp;Value | Description                                                                                                                     |
|----------|---------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| `with`   | `array`                                     | Array of objects                                                                                                                                                         | Yes                           | Array of elements to match with                                                                                                 |
| `as`     | `string`                                    |                                                                                                                                                                          | Yes                           | The name the elements from this entry will be referred as                                                                       |
| `on`     | Transformer(`##current`,`##index`,`##{as}`) | Input is `##current` (current element), `##index` (current index) & `##{as}` the matched element from the array (replace `{as}` with the name provided in `as` argument) | Yes                           | Evaluated condition on when to match an element from the main array and `with` array (uses the [Truthy logic](../truthy-logic.md)) |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
{
  "a": [
    { "id": 2, "a": 1 },
    { "id": 5, "a": 2 }
  ],
  "b":[
    { "id": 2, "a": "x" },
    { "id": 5, "e": true }
  ]
}
```
```transformers
{
  "$$lookup": "$.a",
  "using": [
    {
      "with": "$.b",
      "as": "match",
      "on": {
        "$$is": "##current.id",
        "eq": "##match.id"
      }
    }
  ]
}
```
```json
[
  { 
    "id": 2, 
    "a": "x"
  },
  { 
    "id": 5, 
    "a": 2, 
    "e": true
  }
]
```

```json
{
  "a": [
    { "id": 2, "a": 1 },
    { "id": 5, "a": 2 }
  ],
  "b":[
    { "key": 2, "a": "x" },
    { "key": 5, "e": true }
  ]
}
```
```transformers
{
  "$$lookup": "$.a",
  "using": [
    {
      "with": "$.b",
      "as": "match",
      "on": {
        "$$is": "##current.id",
        "eq": "##match.key"
      }
    }
  ]
}
```
```json
[
  { 
    "id": 2, 
    "a": "x", 
    "key": 2
  },
  { 
    "id": 5, 
    "a": 2, 
    "e": true, 
    "key": 5
  }
]
```


```json
{
  "a": [
    { "id": 2, "a": 1 },
    { "id": 5, "a": 2 }
  ],
  "b":[
    { "id": 2, "a": "x" },
    { "id": 5, "e": true }
  ]
}
```
```transformers
{
  "$$lookup": "$.a",
  "using": [
    {
      "with": "$.b",
      "as": "match",
      "on": {
        "$$is": "##current.id",
        "eq": "##match.id"
      }
    }
  ],
  "to": {
    "*": "##current",
    "e": "##match.e"
  }
}
```
```json
[
  { 
    "id": 2, 
    "a": 1
  },
  {
    "id": 5,
    "a": 2, 
    "e": true
  }
]
```

```json
{
    "a1":[
        { "id": "aaa", "val": "a" },
        { "id": "bbb", "val": "b" }
    ],
    "a2": [
        { "name": "aaa", "val": "A" },
        { "name": "bbb", "val": "B" }
    ]
}
```
```transformers
{
  "$$lookup": "$.a1",
  "using": [
    {
      "with": "$.a2",
      "as": "a2",
      "on": {
        "$$is": "##current.id",
        "eq": "##a2.name"
      }
    }
  ],
  "to": [ "##current.val", "##a2.val"]
}
```
```json
[
   [ 
     "a",
     "A" 
   ], 
  [
    "b",
    "B"
  ]
]
```
```mdx-code-block
</div>
```

