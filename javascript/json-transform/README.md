# @nlighten/json-transform

![](https://img.shields.io/npm/v/@nlighten/json-transform.svg)

[JSON transformers](https://nlighten-oss.github.io/json-transform/) JavaScript implementation (~20kB)

# Installation

`npm install @nlighten/json-transform`

# Usage

```typescript
(async() => { // in an async context
  
  const transformer = new JsonTransformer("$$lower:$.x")
  expect(
    await transformer.transform({x: "HELLO"})
  ).toEqual("hello");
  
})();
```

# Peer dependencies
Notice the following peer dependencies that are required by some of the functions:
 - fast-json-patch
 - js-yaml
 - json-pointer
 - xml2js

# License
[MIT](./LICENSE)