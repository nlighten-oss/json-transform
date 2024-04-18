# $$csv

Converts an array of objects/arrays to a CSV string

### Usage
```transformers
{
  "$$csv": [ /* values */ ],
  "no_headers": false,
  "force_quote": false,
  "separator": ",",
  "names": [ /* names */]
}
```
```transformers
"$$csv([no_headers],[force_quote],[separator],[names]):{input}"
```
### Returns
`string`
### Arguments
| Argument      | Type       | Values         | Required / Default&nbsp;Value       | Description                                                                                          |
|---------------|------------|----------------|-------------------------------------|------------------------------------------------------------------------------------------------------|
| `no_headers`  | `boolean`  | `false`/`true` | `false`                             | Whether to include object keys as headers (taken from first object if no `names`)                    |
| `force_quote` | `boolean`  | `false`/`true` | `false`                             | Whether to quote all the values                                                                      |
| `separator`   | `string`   | `false`/`true` | `","`                               | Use an alternative field separator                                                                   |
| `names`       | `string[]` |                | (Names taken from the first object) | Names of fields to extract into csv if objects (will be used as the header row, unless `no_headers`) |

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
    "a": "A",
    "b": 1
  },
  { 
    "a": "C",
    "b": 2
  }
]
```
```transformers
{
  "$$csv": "$"
}
```
```csv
a,b
A,1
C,2
```

```json
[
  {
    "a": "A",
    "b": 1
  },
  {
    "a": "C",
    "b": 2
  }
]
```
```transformers
{
  "$$csv": "$",
  "no_headers": true
}
```
```csv
A,1
C,2
```

```json
[
  ["1","2"],
  ["3","4"]
]
```
```transformers
{
  "$$csv": "$"
}
```
```csv
1,2
3,4
```

```json
[
  ["1","2"],
  ["3","4"]
]
```
```transformers
{
  "$$csv": "$",
  "names": ["a","b"]
}
```
```csv
a,b
1,2
3,4
```

```json
[
  { 
    "a": "A",
    "b": 1
  },
  { 
    "a": "C",
    "b": 2
  }
]
```
```transformers
"$$csv:$"
```
```csv
a,b
A,1
C,2
```

```json
[
  {
    "a": "A",
    "b": 1
  },
  {
    "a": "C",
    "b": 2
  }
]
```
```transformers
"$$csv(true):$"
```
```csv
A,1
C,2
```


```json
[
  {
    "a": "A",
    "b": 1
  },
  {
    "a": "C",
    "b": 2
  }
]
```
```transformers
"$$csv(true,true):$"
```
```csv
"A","1"
"C","2"
```

```mdx-code-block
</div>
```