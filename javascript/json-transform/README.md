# @nlighten/json-transform

![](https://img.shields.io/npm/v/@nlighten/json-transform.svg)

[JSON transformers](https://nlighten-oss.github.io/json-transform/) JavaScript implementation

# Installation

`npm install @nlighten/json-transform`

# Usage

```typescript
    const transformer = new JsonTransformer("$$lower:$.x")
    expect(
        transformer.transform({ x: "HELLO" })
    ).toEqual("hello");
```

# License
[MIT](./LICENSE)