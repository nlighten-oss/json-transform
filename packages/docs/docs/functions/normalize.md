# $$normalize

Replace special characters forms with their simple form equivalent (removing marks by default)
- Allows post-processing over Java's normalizer algorithm result

#### Post Operations
- `ROBUST` - Try to return the most of similar letters to latin, replaced to their latin equivalent, including:
  - Removing combining diacritical marks (works with `NFD`/`NFKD` which leaves the characters decomposed)
  - Stroked (and others which are not composed) (i.e. `"ĐŁłŒ"` -> `"DLlOE"`)
  - Replacing (with space) and trimming white-spaces


### Usage
```transformers
"$$normalize([form],[postOperation]):{input}"
```
### Returns
`string`
### Arguments
| Argument      | Type   | Values                    | Required / Default&nbsp;Value | Description                                                    |
|---------------|--------|---------------------------|-------------------------------|----------------------------------------------------------------|
| `form`        | `Enum` | `NFKD`/`NFD`/`NKFC`/`NFC` | `NFKD`                        | Normalizer Form (as described in Java's docuemntation)         |
| `postOperation` | `Enum` | `ROBUST`/`NONE`           | `ROBUST`                    | Post operation to run on result to remove/replace more letters |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
"Tĥïŝ ĩš â fůňķŷ Šťŕĭńġ ａｂｃＡＢＣ…"
```
```transformers
"$$normalize:$"
```
```json
"This is a funky String abcABC..."
```

```json
"Tĥïŝ ĩš â fůňķŷ Šťŕĭńġ ａｂｃＡＢＣ…"
```
```transformers
"$$normalize(NFKD,NONE):$"
```
```json
"Tĥïŝ ĩš â fůňķŷ Šťŕĭńġ abcABC..."
```

```json
"ĐŁłŒœÆæǢǣǼǽ"
```
```transformers
"$$normalize:$"
```
```json
"DLlOEoeAEaeAEaeAEae"
```
```mdx-code-block
</div>
```
