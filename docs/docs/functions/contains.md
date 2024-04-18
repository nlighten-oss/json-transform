# $$contains

Checks whether an array contains a certain value

### Usage
```transformers
{
  "$$contains": [ /* arrays / elements */ ],
  "that": /* value */
}
```
```transformers
"$$contains(<that>):{input}"
```
### Returns
`boolean`

### Arguments
| Argument | Type    | Values | Required / Default&nbsp;Value | Description                                            |
|----------|---------|--------|-------------------------------|--------------------------------------------------------|
| Primary  | `array` |        | Yes                           | Array of arrays / elements (null elements are ignored) |
| `that`   | Any     |        |                               | Value to test for                                      |


## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
[0, [], "a"]
```
```transformers
{ 
  "$$contains": "$", 
  "that": "a" 
}
```
```json
true
```


```json
[0, [], "a"]
```
```transformers
{ 
  "$$contains": "$", 
  "that": "b" 
}
```
```json
false
```


```json
[0, [], "a"]
```
```transformers
"$$contains(a):$"
```
```json
true
```


```json
[0, [], "a"]
```
```transformers
"$$contains(b):$"
```
```json
false
```


```mdx-code-block
</div>
```

