# $$range

Creates an array with a sequence of numbers starting with `start` up-to `end` in steps of `step`

### Usage
```transformers
"$$range(<start>,<end>,[step])"
```
### Returns
`number[]`
### Arguments
| Argument | Type     | Values | Required / Default&nbsp;Value | Description                                                         |
|----------|----------|--------|-------------------------------|---------------------------------------------------------------------|
| `start`  | `number` |        | Yes                           | First value                                                         |
| `end`    | `number` |        | Yes                           | Max value to appear in sequence                                     |
| `step`   | `number` |        | `1`                           | Step to add on each iteration to the previous value in the sequence |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**

```json
```
```transformers
"$$range(1,3)"
```
```json
[1,2,3]
```

```json
```
```transformers
"$$range(2,6,2)"
```
```json
[2,4,6]
```

```json
```
```transformers
"$$range(2,5,2)"
```
```json
[2,4]
```

```mdx-code-block
</div>
```
