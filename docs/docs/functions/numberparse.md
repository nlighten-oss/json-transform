# $$numberparse

Parses a number from string

### Usage
```transformers
"$$numberparse(<pattern>,[locale],[grouping],[decimal]):{input}"

"$$numberparse(BASE,[radix]):{input}"
```

### Returns
`number`

### Arguments
| Argument   | Type      | Values                           | Required / Default&nbsp;Value | Description                                                                            |
|------------|-----------|----------------------------------|-------------------------------|----------------------------------------------------------------------------------------|
| `pattern`  | `String`  | `DecimalFormat` pattern / `BASE` | Yes                           | See [tutorial](https://docs.oracle.com/javase/tutorial/i18n/format/decimalFormat.html) |
| `locale`   | `Enum`    | A language tag (e.g. `en-US`)    | `en-US`                       | Locale to use (language and country specific formatting; set by Java)                  |
| `grouping` | `String`  | Single character string          | `,`                           | A custom character to be used for grouping                                             |
| `decimal`  | `String`  | Single character string          | `.`                           | A custom character to be used for decimal point                                        |
| `radix`    | `Integer` |                                  | `10`                          | Radix to be used in interpreting input                                                 |

## Examples

```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**

```json
"123,456,789.88"
```
```transformers
"$$numberparse('#,##0.00'):$"
```
```json
123456789.88
```

```json
"123.456.789,88"
```
```transformers
"$$numberparse('#,##0.00',en-US,'.',','):$"
```
```json
123456789.88
```

```json
"75bcd15"
```
```transformers
"$$numberparse(BASE,16):$"
```
```json
123456789
```

```json
"00001010"
```
```transformers
"$$numberparse(BASE,2):$"
```
```json
10
```

```mdx-code-block
</div>
```