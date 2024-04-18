# $$numberformat

Formats a number

### Usage
```transformers
"$$numberformat(<type>,[locale],[compact_style]):{input}"

"$$numberformat(DECIMAL,[locale],[pattern],[grouping],[decimal]):{input}"

"$$numberformat(BASE,[radix]):{input}"
```

### Returns
`string`
### Arguments
| Argument        | Type      | Values                                                             | Required / Default&nbsp;Value | Description                                                                            |
|-----------------|-----------|--------------------------------------------------------------------|-------------------------------|----------------------------------------------------------------------------------------|
| `type`          | `Enum`    | `NUMBER`/`DECIMAL`/`CURRENCY`/`PERCENT`/`INTEGER`/`COMPACT`/`BASE` | Yes                           | Type of output format                                                                  |
| `locale`        | `Enum`    | A language tag (e.g. `en-US`)                                      | `en-US`                       | Locale to use (language and country specific formatting; set by Java)                  |
| `compact_style` | `Enum`    | `SHORT`/`LONG`                                                     | `SHORT`                       | Effective when `type == COMPACT`, choose which type of compact                         |
| `pattern`       | `String`  | `DecimalFormat` pattern                                            | `#0.00`                       | See [tutorial](https://docs.oracle.com/javase/tutorial/i18n/format/decimalFormat.html) |
| `grouping`      | `String`  | Single character string                                            | `,`                           | A custom character to be used for grouping                                             |
| `decimal`       | `String`  | Single character string                                            | `.`                           | A custom character to be used for decimal point                                        |
| `radix`         | `Integer` |                                                                    | `10`                          | Radix to be used for formatting input                                                  |


## Examples

```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
123456789.87654321
```
```transformers
"$$numberformat(DECIMAL):$"
```
```json
"123456789.88"
```


```json
123456789.87654321
```
```transformers
"$$numberformat(DECIMAL,en-US,'#,##0.00'):$"
```
```json
"123,456,789.88"
```


```json
123456789.87654321
```
```transformers
"$$numberformat(DECIMAL,en-US,'#,##0.00','.',','):$"
```
```json
"123.456.789,88"
```


```json
123456789.87654321
```
```transformers
"$$numberformat(CURRENCY):$"
```
```json
"$123,456,789.88"
```


```json
123456789.87654321
```
```transformers
"$$numberformat(CURRENCY,en-GB):$"
```
```json
"£123,456,789.88"
```


```json
123456789.87654321
```
```transformers
"$$numberformat(PERCENT):$"
```
```json
"12,345,678,988%"
```


```json
123456789.87654321
```
```transformers
"$$numberformat(INTEGER):$"
```
```json
"123,456,790"
```


```json
123456789.87654321
```
```transformers
"$$numberformat(COMPACT):$"
```
```json
"123M"
```


```json
123456789.87654321
```
```transformers
"$$numberformat(COMPACT,en-US,LONG):$"
```
```json
"123 million"
```

```json
123456789
```
```transformers
"$$numberformat(BASE,16):$"
```
```json
"75bcd15"
```

```json
10
```
```transformers
"$$numberformat(BASE,2):$"
```
```json
"1010"
```

```mdx-code-block
</div>
```
