# $$xmlparse

Parses an XML and converts it to an object

- Elements with text and attributes will be converted to objects with a `$content` field for the text

### Usage
```transformers
{
  "$$xmlparse": /* XML string */,
  "keep_strings": false,
  "cdata_tag_name": "$content",
  "convert_nil_to_null": false,
  "force_list": [ /* tag names */ ]  
}
```
```transformers
"$$xmlparse([keep_strings],[convert_nil_to_null]):{input}"
```
### Returns
`object`

### Arguments
| Argument              | Type       | Values         | Required / Default&nbsp;Value | Description                                                           |
|-----------------------|------------|----------------|-------------------------------|-----------------------------------------------------------------------|
| Primary               | `string`   |                | Yes                           | XML string                                                            |
| `keep_strings`        | `boolean`  | `false`/`true` | `false`                       | Do not try to detect primitive types (e.g. numbers, boolean, etc)     |
| `cdata_tag_name`      | `string`   |                | `"$content"`                  | A key for the CDATA section                                           |
| `convert_nil_to_null` | `boolean`  | `false`/`true` | `false`                       | If values with attribute `xsi:nil="true"` will be converted to `null` |
| `force_list`          | `string[]` |                | `[]`                          | Tag names that will always be parsed as arrays                        |

## Examples

```mdx-code-block
<div className="examples_grid">
```

**Input (String)**

**Definition**

**Output**

```xml
<root></root>
```
```transformers
{
  "$$xmlparse": "$"
}
```
```json
{ "root": "" }
```

```xml
<root>
  <hello to="world">
    <hi />
    <hi />
  </hello>
</root>
```
```transformers
{
  "$$xmlparse": "$"
}
```
```json
{
  "root": { 
    "hello": { 
      "hi": [ "", "" ], 
      "to": "world"
    }
  }
}
```


```xml
<root>
  <hello to="world">
    <hi><test>X</test></hi>
  </hello>
</root>
```
```transformers
{
  "$$xmlparse": "$"
}
```
```json
{
  "root": {
    "hello": {
      "hi": { "test": "X" },
      "to": "world"
    }
  }
}
```

```xml
<root>
  <hello to="world">
    <hi><test>X</test></hi>
  </hello>
</root>
```
```transformers
{
  "$$xmlparse": "$",
  "force_list": ["hi"]
}
```
```json
{
  "root": {
    "hello": {
      "hi": [{ "test": "X" }],
      "to": "world"
    }
  }
}
```


```xml
<root>
  <hello to="2">
    <hi>true</hi>
  </hello>
</root>
```
```transformers
{
  "$$xmlparse": "$"
}
```
```json
{
  "root": {
    "hello": {
      "hi": true,
      "to": 2
    }
  }
}
```

```xml
<root>
  <hello to="2">
    <hi>true</hi>
  </hello>
</root>
```
```transformers
{
  "$$xmlparse": "$",
  "keep_strings": true
}
```
```json
{
  "root": {
    "hello": {
      "hi": "true",
      "to": "2"
    }
  }
}
```

```xml
<root></root>
```
```transformers
"$$xmlparse:$"
```
```json
{ "root": "" }
```


```xml
<root>
  <hello to="world">
    <hi />
    <hi />
  </hello>
</root>
```
```transformers
"$$xmlparse:$"
```
```json
{
  "root": { 
    "hello": { 
      "hi": [ "", "" ], 
      "to": "world"
    }
  }
}
```

```xml
<root>
  <hello to="2">
    <hi>true</hi>
  </hello>
</root>
```
```transformers
"$$xmlparse:$"
```
```json
{
  "root": {
    "hello": {
      "hi": true,
      "to": 2
    }
  }
}
```

```xml
<root>
  <hello to="2">
    <hi>true</hi>
  </hello>
</root>
```
```transformers
"$$xmlparse(true):$"
```
```json
{
  "root": {
    "hello": {
      "hi": "true",
      "to": "2"
    }
  }
}
```


```mdx-code-block
</div>
```