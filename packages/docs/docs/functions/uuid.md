# $$uuid

Format and transform UUID

:::info
Input must be a UUID in standard string format (RFC 4122; with hyphens), can be used in conjunction with `#uuid`
:::

### Usage
```transformers
"$$uuid([format]):{input}"
```
### Returns
`string`
### Arguments
| Argument    | Type     | Values                                                        | Required / Default&nbsp;Value | Description                                 |
|-------------|----------|---------------------------------------------------------------|-------------------------------|---------------------------------------------|
| `format`    | `Enum`   | `CANONICAL`/`NO_HYPHENS`/`BASE62`/`BASE64`/`BASE36`/`V3`/`V5` | `CANONICAL`                   | Formatting (or generation in case of v3/v5) |
| `namespace` | `String` | (Must be in UUID format)                                      |                               | UUID to be used as salt (for V3/V5)         |

- `NO_HYPHENS` (can also be specified as `N`) - Same as canonical with hyphens removed
- `BASE36` (can also be specified as `B36`) - alphanumeric alphabet
- `BASE62` (can also be specified as `B62`) - alphanumeric alphabet, case sensitive
- `BASE64` (can also be specified as `B64`) - "URL and Filename safe Base64 Alphabet"
- `V3` - Consider input as name and generate a UUIDv3 (name-based, RFC 4122) (namespace optionally used)
- `V5` - Consider input as name and generate a UUIDv5 (name-based, RFC 4122) (namespace optionally used)

## Examples

```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
"a8e41dc6-74c9-42c5-bb03-3bfd623044c5"
```
```transformers
"$$uuid(N):$"
```
```json
"a8e41dc674c942c5bb033bfd623044c5"
```


```json
"a8e41dc6-74c9-42c5-bb03-3bfd623044c5"
```
```transformers
"$$uuid(BASE36):$"
```
```json
"9zye6dau0hvwo54msqyyjyzt1"
```


```json
"a8e41dc6-74c9-42c5-bb03-3bfd623044c5"
```
```transformers
"$$uuid(BASE62):$"
```
```json
"58gxXh69c8X7f2Id3D84W5"
```


```json
"a8e41dc6-74c9-42c5-bb03-3bfd623044c5"
```
```transformers
"$$uuid(BASE64):$"
```
```json
"qOQdxnTJQsW7Azv9YjBExQ"
```

```json
"widget/1234567890"
```
```transformers
"$$uuid(v3,4bdbe8ec-5cb5-11ea-bc55-0242ac130003):$"
```
```json
"53564aa3-4154-3ca5-ac90-dba59dc7d3cb"
```

```json
"widget/1234567890"
```
```transformers
"$$uuid(v3):$"
```
```json
"d904c507-ee93-3794-9a56-22b6f37cbfe6"
```


```json
"widget/1234567890"
```
```transformers
"$$uuid(v5,4bdbe8ec-5cb5-11ea-bc55-0242ac130003):$"
```
```json
"a35477ae-bfb1-5f2e-b5a4-4711594d855f"
```


```mdx-code-block
</div>
```
