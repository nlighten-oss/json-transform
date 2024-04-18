# $$date

Date formatting utility

:::note
input must be a Date in ISO-8601 format or Date or Instant
:::

:::tip
Useful to be used in conjunction with `#now` as input
:::

### Usage
```transformers
"$$date([format], ...):{input}"
```

## ISO

Returns the ISO-8601 representation of the input date (to the specified precision set by `digits`)

### Usage
```transformers
"$$date(ISO,[digits]):{input}"
```
### Returns
`string` (`date-time` format)
### Arguments
| Argument   | Type      | Values       | Required / Default&nbsp;Value | Description                                                                                  |
|------------|-----------|--------------|-------------------------------|----------------------------------------------------------------------------------------------|
| `digits`   | `integer` | `0`/`3`/`-1` | `-1`                          | Precision of seconds decimal digits (-1 means max; machine dependant, usually nanoseconds)   |

### Examples

```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**

```json
"2020-12-31"
```
```transformers
"$$date(ISO):$"
```
```json
"2020-12-31T00:00:00Z"
```

```json
"2020-12-31T00:00:00.123Z"
```
```transformers
"$$date(ISO,0):$"
```
```json
"2020-12-31T00:00:00Z"
```

```mdx-code-block
</div>
```

## GMT

RFC-1123 format

### Usage
```transformers
"$$date(GMT):{input}"
```
### Returns
`string`

### Examples

```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**

```json
"2020-12-31T23:59:00.000Z"
```
```transformers
"$$date(GMT):$"
```
```json
"Thu, 31 Dec 2020 23:59:00 GMT"
```

```mdx-code-block
</div>
```

## DATE

Date part of ISO 8601,

### Usage
```transformers
"$$date(DATE):{input}"
```
### Returns
`string` (`date` format)

### Examples

```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**

```json
"2020-12-31T23:59:00.000Z"
```
```transformers
"$$date(DATE):$"
```
```json
"2020-12-31"
```

```mdx-code-block
</div>
```

## EPOCH

Seconds passed since 1970-01-01; unless `type`=`MS` then milliseconds,

### Usage
```transformers
"$$date(EPOCH,[resolution]):{input}"
```
### Returns
`integer`
### Arguments
| Argument     | Type    | Values    | Required / Default&nbsp;Value | Description         |
|--------------|---------|-----------|-------------------------------|---------------------|
| `resolution` | `Enum`  | `MS`/`S`  | `S`                           | Resolution of epoch |

### Examples

```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**

```json
"2020-12-31T23:59:00.000Z"
```
```transformers
"$$date(EPOCH):$"
```
```json
1609451940
```

```json
"2020-12-31T23:59:00.000Z"
```
```transformers
"$$date(EPOCH,MS):$"
```
```json
1609451940000
```

```mdx-code-block
</div>
```

## FORMAT

Format using a date format pattern (Java style)

### Usage
```transformers
"$$date(FORMAT,<pattern>,[timezone]):{input}"
```
### Returns
`string`
### Arguments
| Argument   | Type     | Values                             | Required / Default&nbsp;Value | Description      |
|------------|----------|------------------------------------|-------------------------------|------------------|
| `pattern`  | `String` |                                    | Yes                           | Pattern to use   |  
| `timezone` | `Enum`   | Java's `ZoneId` (with `SHORT_IDS`) | `"UTC"`                       | Time zone to use |

### Examples

```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**

```json
"2020-12-31T23:59:00.000Z"
```
```transformers
"$$date(FORMAT,'dd.MM.yyyy'):$"
```
```json
"31.12.2020"
```

```json
"2023-01-01T00:00:00Z"
```
```transformers
"$$date(format,'yyyy-MM-dd HH:mm','America/New_York'):$"
```
```json
"2022-12-31 19:00"
```

```mdx-code-block
</div>
```

## ADD / SUB

Add or subtract by chronological unit (`ChronoUnit`, see Java docs) and amount respectively

### Usage
```transformers
"$$date(ADD,<units>,<amount>):{input}"
```
- Or: 
```transformers
"$$date(SUB,<units>,<amount>):{input}"
```
### Returns
`string` (`date-time` format)
### Arguments
| Argument | Type                  | Values                             | Required / Default&nbsp;Value | Description                   |
|----------|-----------------------|------------------------------------|-------------------------------|-------------------------------|
| `units`  | `Enum` (`ChronoUnit`) | `SECONDS`/`MINUTES`/`DAYS`/ etc... | Yes                           | The units to add or subtract  |  
| `amount` | `integer`             |                                    | Yes                           | The amount to add or subtract |

### Examples

```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**

```json
"2020-12-31T23:59:00.000Z"
```
```transformers
"$$date(ADD,DAYS,1):$"
```
```json
"2021-01-01T23:59:00Z"
```

```json
"2020-12-31T23:59:00.000Z"
```
```transformers
"$$date(SUB,DAYS,1):$"
```
```json
"2020-12-30T23:59:00Z"
```

```mdx-code-block
</div>
```

## ZONE

ISO-8601 with offset by specifying a timezone

### Usage
```transformers
"$$date(ZONE,<zone>):{input}"
```
### Returns
`string` (`date-time` format)
### Arguments
| Argument | Type   | Values                             | Required / Default&nbsp;Value | Description      |
|----------|--------|------------------------------------|-------------------------------|------------------|
| `zone`   | `Enum` | Java's `ZoneId` (with `SHORT_IDS`) | `"UTC"`                       | Time zone to use |
