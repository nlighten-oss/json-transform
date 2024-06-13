# $$length

Returns the length of a value

### Usage
```transformers
"$$length([type],[default_zero]):{input}"
```
### Returns
`integer`

### Arguments
| Argument       | Type                      | Values                           | Required / Default&nbsp;Value | Description                                                                                              |
|----------------|---------------------------|----------------------------------|-------------------------------|----------------------------------------------------------------------------------------------------------|
| Primary        | `string`/`array`/`object` |                                  | Yes                           | Value to check length of                                                                                 |
| `type`         | `Enum`                    | `AUTO`/`STRING`/`ARRAY`/`OBJECT` | `AUTO`                        | Restrict the type of value to check length of (if specified type not detected the result will be `null`) |
| `default_zero` | `boolean`                 | `true`/`false`                   | `false`                       | Whether to return 0 instead of null (on any kind of issue)                                               |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
"hello world"
```
```transformers
"$$length:$"
```
```json
11
```

```json
"hello world"
```
```transformers
"$$length(STRING):$"
```
```json
11
```

```json
["a","b","c"]
```
```transformers
"$$length:$"
```
```json
3
```


```json
{
  "a": 1,
  "b": 2,
  "c": 3,
  "d": 4
}
```
```transformers
"$$length:$"
```
```json
4
```


```json
{
  "a": 1,
  "b": 2,
  "c": 3,
  "d": 4
}
```
```transformers
"$$length(STRING):$"
```
```json
null
```

```json
[1,2,3]
```
```transformers
"$$length(STRING,true):$"
```
```json
0
```


```mdx-code-block
</div>
```
