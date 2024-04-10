# $$sort


Sorts elements of an array 

### Usage
```transformers
{
  "$$sort": [ /* values */ ],
  "by": /* Transformer */,
  "order":  /* direction of ordering */,
  "type": /* type of values (to order by) */,
  "then": [
    {
      "by": /* Transformer */,
      "order":  /* direction of ordering */,
      "type": /* type of values (to order by) */,
    },
    ...
  ]
}
```
### Returns
`array` (same as input)

### Arguments
| Argument | Type        | Values                                          | Required / Default&nbsp;Value | Description                                                                                                                                                             |
|----------|-------------|-------------------------------------------------|-------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Primary  | `array`     |                                                 | Yes                           | Array of elements                                                                                                                                                       |
| `by`     | Transformer | Input is `##current` (current element)          | `##current`                   | A transformer to extract a property to sort by (should resolve to one of the supported types)                                                                           |
| `order`  | `Enum`      | `ASC`/`DESC`                                    | `ASC`                         | Direction of ordering (ascending / descending)                                                                                                                          |
| `type`   | `Enum`      | `auto`/`string`/`number`/`boolean`              | `auto`                        | Type of values to expect when ordering the input array                                                                                                                  |
| `then`   | `array`     | Array of items containing `by`,`order` & `type` |                               | An array of secondary sorting in case previous sorting returned equal, first being the root fields. (Every item require the `by` field while other fields are optional) |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
["b","c","a"]
```
```transformers
{ 
  "$$sort":"$" 
}
```
```json
["a","b","c"]
```

```json
[2,3,1]
```
```transformers
{ 
  "$$sort":"$" 
}
```
```json
[1,2,3]
```

```json
[2,3,1]
```
```transformers
{ 
  "$$sort":"$", 
  "order":"DESC" 
}
```
```json
[3,2,1]
```

```json
[
  [0,"b"],
  [1,"c"],
  [2,"a"]
]
```
```transformers
{ 
  "$$sort":"$", 
  "by":"##current[1]" 
}
```
```json
[
  [2,"a"],
  [0,"b"],
  [1,"c"]
]
```

```json
[
  "D1", 
  "D4", 
  "C2", 
  "B1", 
  "B2", 
  "B3", 
  "A2"
]
```
```transformers
{
  "$$sort": "$",
  "by": "$$substring(0,1):##current",
  "order":"DESC",
  "then": [
    { "by": "$$long:$$substring(-1):##current", "order": "DESC" }
  ]
}
```
```json
[
  "D4", 
  "D1", 
  "C2", 
  "B3", 
  "B2",
  "B1", 
  "A2"
]
```
```mdx-code-block
</div>
```
