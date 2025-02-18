# $$template

Renders a specified text template with the given input or specified payload.

### Usage
```transformers
"$$template([payload],[default_resolve],[url_encode]):{input}"
```
```transformers
{
  "$$template": /* template */,
  "payload": /* any */,
  "default_resolve": "UNIQUE",
  "url_encode": false
}
```
### Returns
`string`
### Arguments
| Argument           | Type      | Values                              | Required / Default&nbsp;Value | Description                                                      |
|--------------------|-----------|-------------------------------------|-------------------------------|------------------------------------------------------------------|
| Primary            | `String`  |                                     | Yes                           | The text template to render                                      |
| `payload`          |           |                                     |                               | Additional context, referred to as `##current` from the template |
| `default_resolve`* | `Enum`    | `UNIQUE`/`FIRST_VALUE`/`LAST_VALUE` | `UNIQUE`                      | Resolve default value based on previous default values or not    |
| `url_encode`       | `Boolean` |                                     | `false`                       | URL encode parameters                                            |

#### * Different Types of default parameter resolving options
| Type               | Description                                                      |
|--------------------|------------------------------------------------------------------|
| `UNIQUE` (default) | Each instance of a parameter is resolved to its explicit default |
| `FIRST_VALUE`      | The first default found for the parameter is used by all         |
| `LAST_VALUE`       | The last default found is used by all                            |


## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
{
  "name": "World"
}
```
```transformers
"$$template:Hello {$.name}"
```
```json
"Hello World"
```

```json
{
  "name": "World"
}
```
```transformers
{
    "$$template": "Hello {##current.name}",
    "payload": "$"
}
```
```json
"Hello World"
```

```json
{
  "text": "HELLO"
}
```
```transformers
"$$template:{$$lower:$.text}"
```
```json
"hello"
```

```json
{
  "id": "1"
}
```
```transformers
"$$template:{$.id} \\{type: {$.type,Unknown}}"
```
```json
"1 {type: Unknown}"
```

```json
{
  "href": "https://example.com/"
}
```
```transformers
{
    "$$template": "href={$.href}",
    "payload": "$",
    "url_encode": true
}
```
```json
"href=https%3A%2F%2Fexample.com%2F"
```


```mdx-code-block
</div>
```
