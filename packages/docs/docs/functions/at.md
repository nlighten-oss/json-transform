# $$at

Returns the value of an array at a specific index.

### Usage
```transformers
{
  "$$at": [ /* values */ ],
  "index": -1
}
```
```transformers
"$$at(<index>):{input}"
```
### Returns
_Same as value at specified index_

### Arguments
| Argument | Type     | Values                        | Required / Default&nbsp;Value | Description                                                                                            |
|----------|----------|-------------------------------|-------------------------------|--------------------------------------------------------------------------------------------------------|
| Primary  | `array`  |                               | Yes                           | Array of elements                                                                                      |
| `index`  | `number` | Positive or negative integers | Yes                           | The index of element to return, negative indices will return element from the end (`-n -> length - n`) |  


## Examples

```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**

```json
[1, 2, 4, 8]
```
```transformers
{ 
  "$$at": "$",
  "index": 1 
}
```
```json
1
```

```json
[1, 2, 4, 8]
```
```transformers
{ 
  "$$at": "$",
  "index": -1 
}
```
```json
8
```

```json
[1, 2, 4, 8]
```
```transformers
"$$at(2):$"
```
```json
4
```

```json
[1, 2, 4, 8]
```
```transformers
"$$at($[0]):$"
```
```json
2
```

```mdx-code-block
</div>
```
