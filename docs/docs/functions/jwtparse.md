# $$jwtparse

Parses the JWT token to retrieve its claims

:::caution
This function does not **validate** the JWT. Only returns the claims.
:::

### Usage
```transformers
{
  "$$jwtparse": /* jwt token */
}
```
```transformers
"$$jwtparse:{input}"
```
### Returns
`object` (token's payload)

### Arguments
| Argument | Type     | Values | Required / Default&nbsp;Value | Description |
|----------|----------|--------|-------------------------------|-------------|
| Primary  | `string` |        | Yes                           | JWT token   |

## Examples

```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMifQ.oULSeVU4UsKJL5nxadn3y-HVxNLHeYcDk_YvSt7jb5k"
```
```transformers
{ 
  "$$jwtparse": "$" 
}
```
```json
{
  "sub": "123"
}
```


```json
"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMifQ.oULSeVU4UsKJL5nxadn3y-HVxNLHeYcDk_YvSt7jb5k"
```
```transformers
"$$jwtparse:$"
```
```json
{
  "sub": "123"
}
```

```mdx-code-block
</div>
```
