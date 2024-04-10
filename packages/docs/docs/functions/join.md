# $$join

Joins an array of inputs as strings with an optional delimiter (default is `""`). 
Null values are omitted.

### Usage
```transformers
{
  "$$join": [ /* values */ ],
  "delimiter": /* delimiter string */,
  "prefix": /* prefix string */,
  "suffix": /* suffix string */
}
```
```transformers
"$$join([delimiter],[prefix],[suffix]):{input}"
```
### Returns
`string`

### Arguments
| Argument    | Type     | Values | Required / Default&nbsp;Value | Description                               |
|-------------|----------|--------|-------------------------------|-------------------------------------------|
| Primary     | `array`  |        | Yes                           | Array of elements                         |
| `delimiter` | `string` |        | `""`                          | Delimiter to join any 2 adjacent elements |  
| `prefix`    | `string` |        | `""`                          | A string to prefix the result             |  
| `suffix`    | `string` |        | `""`                          | A string to suffix the result             |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
["a", null, null, "B"]
```
```transformers
{ "$$join": "$" }
```
```json
"aB"
```


```json
["Hello", "World"]
```
```transformers
{ "$$join": "$", "delimiter": " " }
```
```json
"Hello World"
```


```json noInline
["Hello", "World"]
```
```transformers
{ "$$join": "$", "delimiter": " ", "prefix": "<" }
```
```json
"<Hello World"
```

```json noInline
["Hello", "World"]
```
```transformers
{ "$$join": "$", "delimiter": " ", "prefix": "<", "suffix": ">" }
```
```json
"<Hello World>"
```


```json noInline
["a","b"]
```
```transformers
"$$join:$"
```
```json
"ab"
```

```json noInline
["a","b"]
```
```transformers
"$$join(','):$"
```
```json
"a,b"
```

```json noInline
["hello","world"]
```
```transformers
"$$join( ,<):$"
```
```json
"<hello world"
```

```json noInline
["hello","world"]
```
```transformers
"$$join( ,<,>):$"
```
```json
"<hello world>"
```

```mdx-code-block
</div>
```

