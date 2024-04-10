---
sidebar_position: 3
---
# Truthy logic

Some functions use transformers to evaluate boolean conditions.

The following table will explain what is the default implicit behavior of resolved values from such transformers:

| Value (JSON)     | Boolean value | Comment                              |
|------------------|---------------|--------------------------------------|
| **Boolean**      |               | Used as-is                           |
| `true`           | `true`        |                                      |
| `false`          | `false`       |                                      |
| **Null**         |               |                                      |
| `null`           | `false`       |                                      |
| **Number**       |               | True if not zero                     |
| `1`              | `true`        |                                      |
| `-1`             | `true`        |                                      |
| `0.1`            | `true`        |                                      |
| `0`              | `false`       |                                      |
| **String**       |               | True if not empty (JavaScript style) |
| `""`             | `false`       |                                      |    
| `"false"`        | `true`        | Notice: we don't use `parseBoolean`  |
| **Object**       |               | True if any key exists               |
| `{}`             | `false`       |                                      |
| `{ "a": false }` | `true`        |                                      |
| **Array**        |               | True if length > 0                   |
| `[]`             | `false`       |                                      |
| `[false]`        | `true`        |                                      |
| `[null]`         | `true`        |                                      |


:::note
Strings behave different when evaluated with `$$boolean` & `$$not` (unless specified with `JS` as style)
:::