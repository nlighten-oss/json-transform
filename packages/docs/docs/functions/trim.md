# $$trim

Removes whitespaces from sides of string

### Usage
```transformers
"$$trim([type]):{input}"
```
### Returns
`string`
### Arguments
| Argument | Type   | Values                               | Required / Default&nbsp;Value | Description      |
|----------|--------|--------------------------------------|-------------------------------|------------------|
| `type`*  | `Enum` | `BOTH`/`START`/`END`/`INDENT`/`JAVA` | `BOTH`                        | Type of trimming |


#### * Different Types of trimming
| Type             | Java function             |
|------------------|---------------------------|
| `BOTH` (default) | `String::strip()`         |
| `START`          | `String::stripLeading()`  |
| `END`            | `String::stripTrailing()` |
| `INDENT`         | `String::stripIndent()`   |
| `JAVA`           | `String::trim()`          |


## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
"  hello  "
```
```transformers
"$$trim:$"
```
```json
"hello"
```

```json
"  hello  "
```
```transformers
"$$trim(START):$"
```
```json
"hello  "
```

```json
"  hello  "
```
```transformers
"$$trim(END):$"
```
```json
"  hello"
```

**Input (String)**

**Definition**

**Output**


```xml
    <root>
        <hello />
    </root>
```
```transformers
"$$trim(INDENT):$"
```
```xml
<root>
    <hello />
</root>

```

```mdx-code-block
</div>
```
