# $$partition

Partition an array to multiple constant size arrays 

### Usage
```transformers
{
  "$$partition": [ /* values */ ],
  "size": 100
}
```
```transformers
"$$partition([size]):{input}"
```
### Returns
`array` (of arrays of same items type as input)

### Arguments
| Argument | Type     | Values             | Required / Default&nbsp;Value | Description                |
|----------|----------|--------------------|-------------------------------|----------------------------|
| Primary  | `array`  |                    | Yes                           | Array of elements          |
| `size`   | `number` | A positive integer | `100`                         | The size of each partition |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
[
  "a",
  "b",
  "c",
  "d",
  "e",
  "f"
]
```
```transformers
{ 
  "$$partition": "$", 
  "size": 3 
}
```
```json
[
  [
    "a",
    "b",
    "c"
  ],
  [
    "d",
    "e",
    "f"
  ]
]
```


```json
[
  "a",
  "b",
  "c",
  "d",
  "e",
  "f"
]
```
```transformers
"$$partition(3):$"
```
```json
[
  [
    "a",
    "b",
    "c"
  ],
  [
    "d",
    "e",
    "f"
  ]
]
```

```mdx-code-block
</div>
```
