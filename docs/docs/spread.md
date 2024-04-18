---
sidebar_position: 4
---

# Spread

Transformers have a special spread "key operator": `*`.

When `"*"` is used in a transformer as a key inside an object, it will copy all the entries from the resolved object of its value before adding the keys in the spec.

## Example

```mdx-code-block
<div className="examples_grid">
```
```json title="Input"
{
  "base": {
    "language": "en"
  }
}
```
```transformers title="Definition" {2}
{
  "*": "$.base",
  "hello": "world"
}
```
```json title="Output"
{
  "language": "en",
  "hello": "world"
}
```
```mdx-code-block
</div>
```

## Remove keys

The spread operation can be used to remove keys, here is an example:

```mdx-code-block
<div className="examples_grid">
```
```json title="Input"
{
  "base": {
    "id": "short id",
    "name": "short name",
    "description": "some very long description"
  }
}
```
```transformers title="Definition" {3}
{
  "*": "$.base",
  "description": "#null"
}
```
```json title="Output"
{
  "id": "short id",
  "name": "short name"
}
```

```mdx-code-block
</div>
```
Notice we use `#null` to select the keys we want to remove, in this case `description`.



## Multiple sources

You can specify more than one source by setting the value to an array:
```json {2}
{
  "*": ["$.base1", "$.base2"],
  "hello": "world"
}
```
Merge process will be operated in order of array (with the local keys set last as overrides)

This much resembles the `...` spread operator in ECMAScript:
```javascript {2,3}
result = {
  ...base1,
  ...base2,
  hello: "world"
}
```