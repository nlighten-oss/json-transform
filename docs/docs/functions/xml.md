# $$xml

Converts an object to XML string (a wrapper element can be added by specifying the field `root` with the element name).
Optionally runs an XSLT over the result before returning it.

### Usage
```transformers
{
  "$$xml": /* value */,
  "root": /* container name */,
  "xslt": /* transformation spec */
}
```
```transformers
"$$xml([root]):{input}"
```
### Returns
`string` (XML String)

### Arguments
| Argument | Type     | Values | Required / Default&nbsp;Value | Description                                       |
|----------|----------|--------|-------------------------------|---------------------------------------------------|
| Primary  | `object` |        | Yes                           | "XML structured" JSON object                      |
| `root`   | `string` |        |                               | Add a wrapper element with that name              |
| `xslt`   | `string` |        |                               | Transform result XML with the given XSLT document |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output (String)**


```json
{
  "hello": {
    "hi":[ 
      "",
      ""
    ],
    "to": "world"
  }
}
```
```transformers
{ 
  "$$xml": "$" 
}
```
```xml
<hello>
  <hi/><hi/>
  <to>world</to>
</hello>
```

```json
{
  "a": "",
  "b": ""
}
```
```transformers
{ 
  "$$xml": "$" 
}
```
```xml
<a/><b/>
```

```json
{
  "a": "",
  "b": ""
}
```
```transformers
{ 
  "$$xml": "$" 
  "root": "x"
}
```
```xml
<x>
  <a/>
  <b/>
</x>
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="/container">
     <my_record>
       <first><xsl:value-of select="a" /></first>
       <second><xsl:value-of select="b" /></second>
     </my_record>
  </xsl:template>
</xsl:stylesheet>
```
```transformers
{ 
  "$$xml": {
    "a": "AAA",
    "b": "BBB"
  },
  "root": "x", 
  "xslt": "$"
}
```
```xml
<?xml version="1.0" encoding="UTF-8"?>
<my_record>
  <first>AAA</first>
  <second>BBB</second>
</my_record>
```

```json
{
  "hello": {
    "hi": [ "", "" ],
    "to": "world"
  }
}
```
```transformers
"$$xml:$"
```
```xml
<hello>
  <hi/>
  <hi/>
  <to>world</to>
</hello>
```


```json
{
  "a": "",
  "b": ""
}
```
```transformers
"$$xml:$"
```
```xml
<a/>
<b/>
```


```json
{
  "a": "",
  "b": ""
}
```
```transformers
"$$xml(x):$"
```
```xml
<x>
  <a/>
  <b/>
</x>
```


```json
{
  "tag1": {
    "value":"lorem"
  },
  "tag2": {
    "items": [
      {
        "id": 1
      },
      {
        "id": 2
      }
    ]
  }
}
```
```transformers
{
  "$$xml": "$",
  "root":"root",
  /* "XSLT does prettify" */
  "xslt": "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><xsl:output method=\"xml\" indent=\"yes\"/><xsl:strip-space elements=\"*\"/><xsl:template match=\"/\"><xsl:copy-of select=\".\"/></xsl:template></xsl:stylesheet>"
}
```
```xml
<?xml version="1.0" encoding="UTF-8"?><root>
  <tag1>
    <value>lorem</value>
  </tag1>
  <tag2>
    <items>
      <id>1</id>
    </items>
    <items>
      <id>2</id>
    </items>
  </tag2>
</root>
```

```mdx-code-block
</div>
```
