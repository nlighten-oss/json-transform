# $$slice

Returns a slice of an array from a start index to end index (exclusive).

### Usage
```transformers
{
  "$$slice": [ /* values */ ],
  "begin": /* begin */,
  "end": /* end */,
}
```
```transformers
"$$slice(<begin>,[end]):{input}"
```
### Returns
_Same as value at specified index_

### Arguments
| Argument | Type      | Values | Required / Default&nbsp;Value | Description                                                                            |
|----------|-----------|--------|-------------------------------|----------------------------------------------------------------------------------------|
| Primary  | `array`   |        | Yes                           | Array of elements                                                                      |
| `begin`  | `integer` |        | `0`                           | Index of first character to slice from (if negative, counts from the end of the array) |
| `end`    | `integer` |        | End of array                  | Index of last character to slice to (if negative, counts from the end of the array)    |


## Examples

```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**

```json
[0, 1, 2, 3, 4, 5, 6, 7]
```
```transformers
"$$slice(1):$"
```
```json
[1, 2, 3, 4, 5, 6, 7]
```

```json
[0, 1, 2, 3, 4, 5, 6, 7]
```
```transformers
"$$slice(2,6):$"
```
```json
[2, 3, 4, 5]
```

```json
[0, 1, 2, 3, 4, 5, 6, 7]
```
```transformers
"$$slice(3,-1):$"
```
```json
[3, 4, 5, 6]
```

```json
[0, 1, 2, 3, 4, 5, 6, 7]
```
```transformers
"$$slice(-2):$"
```
```json
[6, 7]
```

```json
[0, 1, 2, 3, 4, 5, 6, 7]
```
```transformers
"$$slice(-3,-1):$"
```
```json
[5, 6]
```

```json
[0, 1, 2, 3, 4, 5, 6, 7]
```
```transformers
"$$slice(-2,-1):$"
```
```json
[6]
```

```json
[0, 1, 2, 3]
```
```transformers
"$$slice:$"
```
```json
[0, 1, 2, 3]
```


```mdx-code-block
</div>
```
