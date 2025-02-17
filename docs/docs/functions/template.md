# $$template

Renders a specified text template with the given input or specified payload.

### Usage
```transformers
"$$template([payload],[default_resolve]):{input}"
```
```transformers
{
  "$$template": /* template */,
  "payload": /* any */,
  "default_resolve": "UNIQUE"
}
```
### Returns
`string`
### Arguments
| Argument           | Type     | Values                              | Required / Default&nbsp;Value | Description                                                      |
|--------------------|----------|-------------------------------------|-------------------------------|------------------------------------------------------------------|
| Primary            | `String` |                                     | Yes                           | The text template to render                                      |
| `payload`          |          |                                     |                               | Additional context, referred to as `##current` from the template |
| `default_resolve`* | `Enum`   | `UNIQUE`/`FIRST_VALUE`/`LAST_VALUE` | `UNIQUE`                      | Default resolve option for the template                          |

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

```mdx-code-block
</div>
```
