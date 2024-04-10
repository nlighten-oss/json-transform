# $$map

Returns a mapped array applying the transformer on each of the elements 

### Usage
```transformers
{
  "$$map": [ /* values */ ],
  "to": /* Transformer */
}
```
### Returns
`array` (of type of `to`'s result)
### Arguments
| Argument | Type                               | Values                                                             | Required / Default&nbsp;Value | Description                                                      |
|----------|------------------------------------|--------------------------------------------------------------------|-------------------------------|------------------------------------------------------------------|
| Primary  | `array`                            |                                                                    | Yes                           | Array of elements                                                |
| `to`     | Transformer(`##current`,`##index`) | Input is `##current` (current element) & `##index` (current index) | Yes                           | Transformer to map each element to its value in the result array |

## Alternative form

```transformers
{
  "$$map": [ [ /* values */ ], /* to */]
}
```
### Arguments
| Argument | Type    | Values | Required / Default&nbsp;Value | Description                      |
|----------|---------|--------|-------------------------------|----------------------------------|
| Primary  | `array` |        | Yes                           | Array of size 2 (`values`, `to`) |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
[
  { 
    "a": "A1",
    "id": 5
  }, 
  { 
    "a": "A2",
    "id": 9
  }
]
```
```transformers
{ 
  "$$map": "$", 
  "to": {
    "i": "##current.id", 
    "m": "##current.a", 
    "idx": "##index"
  }
}
```
```json
[
  {
    "i": 5,
    "m": "A1",
    "idx": 0
  },
  { 
    "i": 9,
    "m": "A2",
    "idx": 1
  }
]
```
```mdx-code-block
</div>
```
