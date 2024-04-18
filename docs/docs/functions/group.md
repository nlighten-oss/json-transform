# $$group

Groups an array of elements into a map of key:array

### Usage
```transformers
{
  "$$group": [ /* values */ ],
  "by": /* Transformer */,
  "order":  /* direction of ordering */,
  "type": /* type of values (to order by) */,
  "then": [ /* secondary grouping */
    {
      "by": /* Transformer */,
      "order":  /* direction of ordering */,
      "type": /* type of values (to order by) */,
    },
    ...
  ]
}
```
```transformers
"$$group:{input}"
```
### Returns
`object` (`Map<String, Array>`)

### Arguments
| Argument | Type        | Values                                          | Required / Default&nbsp;Value | Description                                            |
|----------|-------------|-------------------------------------------------|-------------------------------|--------------------------------------------------------|
| Primary  | `array`     |                                                 | Yes                           | Array of elements                                      |
| `by`     | Transformer | Input is `##current` (current element)          | Yes                           | A transformer to extract a property to group by        |
| `order`  | `Enum`      | `ASC`/`DESC`                                    | `ASC`                         | Direction of ordering (ascending / descending)         |
| `type`   | `Enum`      | `auto`/`string`/`number`/`boolean`              | `auto`                        | Type of values to expect when ordering the input array |
| `then`   | `array`     | Array of items containing `by`,`order` & `type` |                               | An array of subsequent grouping.                       |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
[
  { "o": 1, "p": 11, "w":"aaa"},
  { "o": 1, "p": 13, "w":"bbb"},
  { "o": 1, "p": 11, "w":"ccc"},
  { "o": 2, "p": 11, "w":"ddd"},
  { "o": 2, "p": 13, "w":"eee"},
  { "o": 3, "p": 12, "w":"fff"},
  { "o": 1, "p": 9, "w":"zzz"}
]
```
```transformers
{
   "$$group": "$",
   "by": "##current.o",
   "then": [
     {
       "by": { 
         "$$join": [ 
           "p_", 
           "##current.p" 
         ] 
       },
       "order": "DESC"
     }
   ]
 }
```
```json
{
  "1": {
    "p_9": [
      {
        "o": 1,
        "p": 9,
        "w": "zzz"
      }
    ],
    "p_13": [
      {
        "o": 1,
        "p": 13,
        "w": "bbb"
      }
    ],
    "p_11": [
      {
        "o": 1,
        "p": 11,
        "w": "aaa"
      },
      {
        "o": 1,
        "p": 11,
        "w": "ccc"
      }
    ]
  },
  "2": {
    "p_13": [
      {
        "o": 2,
        "p": 13,
        "w": "eee"
      }
    ],
    "p_11": [
      {
        "o": 2,
        "p": 11,
        "w": "ddd"
      }
    ]
  },
  "3": {
    "p_12": [
      {
        "o": 3,
        "p": 12,
        "w": "fff"
      }
    ]
  }
}
```


```json
[
  ["a", 1],
  ["b", true],
  ["c", "C"],
  ["c", "D"]
]
```
```transformers
"$$group:$"
```
```json
{
  "a": [1], 
  "b": [true], 
  "c": ["C", "D"]
}
```

```json
[
  ["a", 0, 1],
  ["a", 1, true],
  ["a", 2, "C"],
  ["b", 1, 6]
]
```
```transformers
"$$group:$"
```
```json
{
  "a": {
    "0": [1],
    "1": [true],
    "2": ["C"]
  },
  "b": {
    "1": [6]
  }
}
```

```mdx-code-block
</div>
```
