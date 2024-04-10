# $$digest

Creates a message digest based on a supported algorithm.

### Usage
```transformers
{
  "$$digest": /* value */,
  "algorithm": "SHA-1",
  "format": "BASE64"
}
```
```transformers
"$$digest([algorithm],[format]):{input}"
```
### Returns
`String` (unless alg is `JAVA` then it's `Integer`)

### Arguments
| Argument    | Type   | Values                                             | Required / Default&nbsp;Value | Description                                                                                                                                                       |
|-------------|--------|----------------------------------------------------|-------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `algorithm` | `Enum` | `SHA-1`/`SHA-256`/`SHA-384`/`SHA-512`/`MD5`/`JAVA` | `SHA-1`                       | Hashing algorithm                                                                                                                                                 |
| `format`    | `Enum` | `BASE64`/`BASE64URL`/`HEX`                         | `BASE64`                      | Format of output (`BASE64` = "The Base64 Alphabet" from RFC-2045, `BAS64URL` = "URL and Filename safe Base64 Alphabet" from RFC-4648, `HEX` = Hexadecimal string) |

## Examples


```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**

```json
"Hello World"
```
```transformers
"$$digest:$"
```
```json
"Ck1VqNd45QIvq3AZd8XYQLvEhtA="
```

```json
"Hello World"
```
```transformers
"$$digest(SHA-1):$"
```
```json
"Ck1VqNd45QIvq3AZd8XYQLvEhtA="
```


```json
"Hello World"
```
```transformers
"$$digest(SHA-1,BASE64):$"
```
```json
"Ck1VqNd45QIvq3AZd8XYQLvEhtA="
```

```json
"Hello World"
```
```transformers
"$$digest(SHA-1,HEX):$"
```
```json
"0a4d55a8d778e5022fab701977c5d840bbc486d0"
```

```json
"Hello World"
```
```transformers
"$$digest(SHA-256):$"
```
```json
"pZGm1Av0IEBKARczz7exkNYsZb8LzaMrV7J32a2fFG4="
```

```json
"Hello World"
```
```transformers
"$$digest(SHA-256,HEX):$"
```
```json
"a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e"
```

```json
"Hello World"
```
```transformers
"$$digest(SHA-384):$"
```
```json
"mVFDKRhrL2rkoTKefubGEKcpY2M1F0rGt0D5AoOW/MgD0Ok4Y6fD2Q+Gvu54L08/"
```

```json
"Hello World"
```
```transformers
"$$digest(SHA-384,BASE64URL):$"
```
```json
"mVFDKRhrL2rkoTKefubGEKcpY2M1F0rGt0D5AoOW_MgD0Ok4Y6fD2Q-Gvu54L08_"
```

```json
"Hello World"
```
```transformers
"$$digest(SHA-384,HEX):$"
```
```json
"99514329186b2f6ae4a1329e7ee6c610a729636335174ac6b740f9028396fcc803d0e93863a7c3d90f86beee782f4f3f"
```

```json
"Hello World"
```
```transformers
"$$digest(SHA-512):$"
```
```json
"LHT9F+2v2A6ER7DUZ0HuJDt+t03SFJoKsbkkb7MDgvJ+hT2FhXGeDmfL2g2qj1FnEGRhXWRa4nrLFb+xRH9Fmw=="
```

```json
"Hello World"
```
```transformers
"$$digest(SHA-512,HEX):$"
```
```json
"2c74fd17edafd80e8447b0d46741ee243b7eb74dd2149a0ab1b9246fb30382f27e853d8585719e0e67cbda0daa8f51671064615d645ae27acb15bfb1447f459b"
```

```json
"Hello World"
```
```transformers
"$$digest(MD5):$"
```
```json
"sQqNsWTgdUEFt6mb5y4/5Q=="
```

```json
"Hello World"
```
```transformers
"$$digest(MD5,HEX):$"
```
```json
"b10a8db164e0754105b7a99be72e3fe5"
```

```json
"Hello World"
```
```transformers
"$$digest(JAVA):$"
```
```json
-862545276
```

```mdx-code-block
</div>
```