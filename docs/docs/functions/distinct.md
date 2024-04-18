# $$distinct

Returns a distinct array (repeating elements removed, only primitive values are supported if no `by` is specified) 

### Usage
```transformers
{
  "$$distinct": [ /* values */ ],
  "by": /* Transformer */
}
```
```transformers
"$$distinct([by]):{input}"
```
### Returns
`array` (same items type as input)
### Arguments
| Argument | Type                     | Values                                 | Required / Default&nbsp;Value | Description                                                              |
|----------|--------------------------|----------------------------------------|-------------------------------|--------------------------------------------------------------------------|
| Primary  | `array`                  |                                        | Yes                           | Array of elements                                                        |
| `by`     | Transformer(`##current`) | Input is `##current` (current element) | `"##current"`                 | A mapping for each element to distinct by (instead of the whole element) |


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
  "b",
  "c",
  "b"
]
```
```transformers
{ 
  "$$distinct": "$" 
}
```
```json
[
  "a",
  "b",
  "c"
]
```


```json
[ 
  "a",
  1,
  false,
  "b",
  "c",
  "b",
  1,
  false,
  false
]
```
```transformers
{ 
  "$$distinct": "$" 
}
```
```json
[
  "a",
  1,
  false,
  "b",
  "c"
]
```


```json
[
  "a",
  "a",
  null,
  null,
  "a",
  null
]
```
```transformers
{
  "$$distinct": "$" 
}
```
```json
[ 
  "a", 
  null
]
```


```json
[
  { "a": 1 },
  { "a": 1 },
  { "a": 1 }
]
```
```transformers
{ 
  "$$distinct": "$" 
}
```
```json
[
  { "a": 1 }
]
```


```json
[
  [ "a", 1 ],
  [ "a", 2 ],
  [ "a", 1 ],
  [ "a", 1 ]
]
```
```transformers
{ 
  "$$distinct": "$" 
}
```
```json
[
  [ "a", 1 ],
  [ "a", 2 ]
]
```


```json
[
  { "a": 1 },
  { 
    "a": 1,
    "b": 1
  },
  { "a": 1,
    "b": 2
  },
  { "a": 2,
    "b": 1
  }
]
```
```transformers
{ 
  "$$distinct": "$", 
  "by": "##current.a" 
}
```
```json
[
  { "a": 1 },
  { 
    "a": 2,
    "b": 1
  }
]
```


```json
[
  { "a": 1 },
  { 
    "a": 1 ,
    "b": 1
  },
  {
    "a": 1,
    "b": 2
  },
  {
    "a": 2,
    "b": 1
  }
]
```
```transformers
{ 
  "$$distinct": "$", 
  "by": "##current.b" 
}
```
```json
[
  { "a": 1 },
  { 
    "a": 1, 
    "b": 1
  },
  { 
    "a": 1, 
    "b": 2
  }
]
```


```mdx-code-block
</div>
```

