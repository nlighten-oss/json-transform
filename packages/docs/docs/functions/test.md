# $$test

Checks if a string matches a certain pattern

### Usage
```transformers
"$$test(<pattern>):{input}"
```
### Returns
`boolean`
### Arguments
| Argument  | Type     | Values | Required / Default&nbsp;Value | Description                                      |
|-----------|----------|--------|-------------------------------|--------------------------------------------------|
| `pattern` | `string` | Regex  | Yes                           | Regular expression to match against input string |  

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
"hello"
```
```transformers
"$$test(he):$"
```
```json
true
```

```json
"hello"
```
```transformers
"$$test(xe):$"
```
```json
false
```

```mdx-code-block
</div>
```
