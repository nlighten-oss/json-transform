# $$merge

Merge multiple objects into one. Allows deep merging and array concatenation.

### Usage
```transformers
{
  "$$merge": [ /* objects */ ],
  "deep": /* true / false (default) */,
  "arrays": /* true / false (default) */
}
```
```transformers
"$$merge([deep],[arrays]):{input}"
```
### Returns
`object` (The merged schema of all specified objects)

### Arguments
| Argument | Type      | Values | Required / Default&nbsp;Value | Description                     |
|----------|-----------|--------|-------------------------------|---------------------------------|
| Primary  | `array`   |        | Yes                           | Array of objects to merge       |
| `deep`   | `boolean` |        | `false`                       | Whether to merge objects deeply |
| `arrays` | `boolean` |        | `false`                       | Whether to concatenate arrays   |

## Examples


```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**

```json
{
  "a": "A"
}
```
```transformers
{
  "$$merge": [
    "$",
    {
      "b": "B"
    }
  ]
}
```
```json
{
  "a": "A",
  "b": "B"
}
```

```json
{
  "a": "A",
  "b": "B"
}
```
```transformers
{
  "$$merge": [
    "$",
    {
      "b": "BB"
    }
  ]
}
```
```json
{
  "a": "A",
  "b": "BB"
}
```

```json
{
  "a": "A",
  "b": "B"
}
```
```transformers
{
  "$$merge": [
    "$",
    {
      "b": "#null"
    }
  ]
}
```
```json
{
  "a": "A",
  "b": null
}
```

```json
{
  "a": {
    "aa": "AA"
  },
  "b": "B"
}
```
```transformers
{
  "$$merge": [
    "$",
    {
      "a": {
        "aaa": "AAA"
      }
    }
  ]
}
```
```json
{
  "a": {
    "aaa": "AAA"
  },
  "b": "B"
}
```

```json
{
  "a": {
    "aa": "AA"
  },
  "b": "B"
}
```
```transformers
{
  "$$merge": [
    "$",
    {
      "a": {
        "aaa": "AAA"
      }
    }
  ],
  "deep": true
}
```
```json
{
  "a": {
    "aa": "AA",
    "aaa": "AAA"
  },
  "b": "B"
}
```

```json
{
  "a": {
    "aa": "AA"
  },
  "c": [
    1,
    2
  ]
}
```
```transformers
{
  "$$merge": [
    "$",
    {
      "a": {
        "aaa": "AAA"
      },
      "c": [
        3,
        4
      ]
    }
  ],
  "deep": true,
  "arrays": true
}
```
```json
{
  "a": {
    "aa": "AA",
    "aaa": "AAA"
  },
  "c": [
    1,
    2,
    3,
    4
  ]
}
```

```json
{
  "a": {
    "aa": "AA"
  },
  "c": [
    1,
    2
  ]
}
```
```transformers
{
  "$$merge": [
    "$",
    {
      "a": {
        "aaa": "AAA"
      },
      "c": [
        3,
        4
      ]
    }
  ],
  "arrays": true
}
```
```json
{
  "a": {
    "aaa": "AAA"
  },
  "c": [
    1,
    2,
    3,
    4
  ]
}
```

```mdx-code-block
</div>
```