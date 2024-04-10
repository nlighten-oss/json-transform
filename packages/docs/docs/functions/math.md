# $$math

Evaluate a mathematical expression

### Usage
```transformers
{
  "$$math": [/* operator */, /* operand 1 */, /* operand 2 ? */]
}
```
- OR
```transformers
{
  "$$math": [/* operand 1 */, /* operator */, /* operand 2 ? */]
}
```
```transformers
"$$math(<operand1>,<operator>,[operand2])"

"$$math(<operator>,<operand1>,[operand2])"

"$$math(<operator>,[operand2]):{operand1}"
```

### Returns
`number`

### Arguments
| Argument   | Type     | Values                        | Required / Default&nbsp;Value | Description       |
|------------|----------|-------------------------------|-------------------------------|-------------------|
| Primary    | `array`  |                               | Yes                           | Array of size 2/3 |
| `operator` | `Enum`   | Described [below](#operators) | Yes                           | Operator          |
| `operand1` | `number` |                               | Yes                           | First operand     |
| `operand2` | `number` |                               |                               | Second operand    |

#### Operators

| `operator`                | Action                 | Example                     |
|---------------------------|------------------------|-----------------------------|
| `+`, `ADD`                | Addition               | `"$$math(2,+,3)"` = `5`     |
| `-`, `SUB`, `SUBTRACT`    | Subtraction            | `"$$math(5,-,3)"` = `2`     |
| `*`, `MUL`, `MULTIPLY`    | Multiplication         | `"$$math(2,*,3)"` = `6`     |
| `/`, `DIV`, `DIVIDE`      | Division               | `"$$math(6,/,3)"` = `2`     |
| `//`, `INTDIV`            | Integer division       | `"$$math(7,//,3)"` = `2`    |
| `%`, `MOD`, `REMAINDER`   | Modulu                 | `"$$math(7,%,3)"` = `1`     |
| `^`, `**`, `POW`, `POWER` | Power                  | `"$$math(2,^,3)"` = `8`     |
| `&`, `AND`                | Bit-wise AND           | `"$$math(6,&,3)"` = `2`     |
| &nbsp;\|&nbsp;, `OR`      | Bit-wise OR            | `"$$math(6,OR,3)"` = `7`    |
| `~`, `XOR`                | Bit-wise XOR           | `"$$math(6,~,3)"` = `5`     |
| `<<`, `SHL`               | Shift left (bit-wise)  | `"$$math(6,>>,1)"` = `3`    |
| `>>`, `SHR`               | Shift right (bit-wise) | `"$$math(6,<<,3)"` = `48`   |
| `MIN`                     | Minimum                | `"$$math(MIN,4,2)"` = `2`   |
| `MAX`                     | Maximum                | `"$$math(MAX,4,2)"` = `4`   |
| `SQRT`                    | Square root            | `"$$math(SQRT,81)"` = `9`   |
| `ROUND`                   | Round                  | `"$$math(ROUND,4.6)"` = `5` |
| `FLOOR`                   | Floor                  | `"$$math(FLOOR,4.6)"` = `4` |
| `CEIL`                    | Ceil                   | `"$$math(CEIL,4.2)"` = `5`  |
| `ABS`                     | Absolute               | `"$$math(ABS,-10)"` = `10`  |
| `NEG`, `NEGATE`           | Negation               | `"$$math(NEG,4)"` = `-4`    |
| `SIG`, `SIGNUM`           | Sign Number            | `"$$math(SIG,-42)"` = `-1`  |

## Examples
```mdx-code-block
<div className="examples_grid">
```

**Input**

**Definition**

**Output**


```json
[4,2]
```
```transformers
{ "$$math": ["+","$[0]","$[1]" ] }
```
```json
6
```

```json
[4,2]
```
```transformers
{ "$$math": ["$[0]","+","$[1]" ] }
```
```json
6
```

```json
[4,2]
```
```transformers
{ "$$math": ["$[0]","-","$[1]" ] }
```
```json
2
```

```json
[4,2]
```
```transformers
{ "$$math": ["$[0]","*","$[1]" ] }
```
```json
8
```

```json
[4,2]
```
```transformers
{ "$$math": ["$[0]","/","$[1]" ] }
```
```json
2
```

```json
[4,2]
```
```transformers
{ "$$math": ["$[0]","//",3] }
```
```json
1
```

```json
[4,2]
```
```transformers
{ "$$math": ["$[0]","%",3] }
```
```json
1
```

```json
[4,2]
```
```transformers
{ "$$math": ["$[1]","^",3] }
```
```json
8
```

```json
[4,2]
```
```transformers
{ "$$math": ["MIN","$[0]","$[1]" ] }
```
```json
2
```

```json
[4,2]
```
```transformers
{ "$$math": ["MAX","$[0]","$[1]" ] }
```
```json
4
```

```json

```
```transformers
{ "$$math": ["SQRT",81] }
```
```json
9
```

```json

```
```transformers
{ "$$math": ["ROUND",4.6] }
```
```json
5
```

```json

```
```transformers
{ "$$math": ["ROUND",4.66,1] }
```
```json
4.7
```

```json

```
```transformers
{ "$$math": ["FLOOR",4.6] }
```
```json
4
```

```json

```
```transformers
{ "$$math": ["FLOOR",4.66,1] }
```
```json
4.6
```

```json

```
```transformers
{ "$$math": ["CEIL",4.2] }
```
```json
5
```

```json

```
```transformers
{ "$$math": ["CEIL",4.22,1] }
```
```json
4.3
```

```json

```
```transformers
{ "$$math": ["ABS",-10] }
```
```json
10
```

```json
[4,2]
```
```transformers
{ "$$math": ["NEG","$[0]"] }
```
```json
-4
```

```json
-11
```
```transformers
{ "$$math": ["SQRT",{ "$$math": ["$","^",2] }] }
```
```json
11
```


```json
[4,2]
```
```transformers
"$$math(+,$[0],$[1])"
```
```json
6
```


```json
[4,2]
```
```transformers
"$$math($[0],+,$[1])"
```
```json
6
```


```json
[4,2]
```
```transformers
"$$math($[0],-,$[1])"
```
```json
2
```


```json
[4,2]
```
```transformers
"$$math($[0],*,$[1])"
```
```json
8
```


```json
[4,2]
```
```transformers
"$$math($[0],/,$[1])"
```
```json
2
```


```json
[4,2]
```
```transformers
"$$math($[0],//,3)"
```
```json
1
```


```json
[4,2]
```
```transformers
"$$math($[0],%,3)"
```
```json
1
```


```json
[4,2]
```
```transformers
"$$math($[1],^,3)"
```
```json
8
```


```json
[4,2]
```
```transformers
"$$math(MIN,$[0],$[1])"
```
```json
2
```


```json
[4,2]
```
```transformers
"$$math(MAX,$[0],$[1])"
```
```json
4
```


```json

```
```transformers
"$$math(SQRT,81)"
```
```json
9
```


```json

```
```transformers
"$$math(SQRT):81"
```
```json
9
```


```json

```
```transformers
"$$math(ROUND,4.6)"
```
```json
5
```


```json

```
```transformers
"$$math(ROUND):4.6"
```
```json
5
```

```json

```
```transformers
"$$math(ROUND,1):4.66"
```
```json
4.7
```

```json

```
```transformers
"$$math(ROUND,4.66,1)"
```
```json
4.7
```

```json

```
```transformers
"$$math(4.66,ROUND,1)"
```
```json
4.7
```

```json

```
```transformers
"$$math(FLOOR,4.6)"
```
```json
4
```


```json
4.66
```
```transformers
"$$math(FLOOR,1):$"
```
```json
4.6
```


```json

```
```transformers
"$$math(CEIL,4.2)"
```
```json
5
```

```json
4.22
```
```transformers
"$$math(CEIL,1):$"
```
```json
4.3
```

```json

```
```transformers
"$$math(ABS,-10)"
```
```json
10
```


```json
[4,2]
```
```transformers
"$$math(NEG,$[0])"
```
```json
-4
```


```json

```
```transformers
"$$math(ROUND):$$math(10,/,4)"
```
```json
3
```


```mdx-code-block
</div>
```
