# json-transform

- [Documentation](https://nlighten-oss.github.io/json-transform/)

## Packages

| Language   |Name| Description                                             |License|Status|
|------------|---|---------------------------------------------------------|---|---|
| Java       |[co.nlighten.json-transform](./java/json-transform)| Java library for transforming JSON objects              |Apache License 2.0|![Maven Central Version](https://img.shields.io/maven-central/v/co.nlighten/json-transform)|
| JavaScript |[@nlighten/json-transform](./javascript/json-transform)| JSON transformers JavaScript implementation|MIT|![npm](https://img.shields.io/npm/v/@nlighten/json-transform)|
| JavaScript |[@nlighten/json-transform-core](./javascript/json-transform-core)| Core types and utilities for handling JSON transformers |MIT|![npm](https://img.shields.io/npm/v/@nlighten/json-transform-core)|
| JavaScript |[@nlighten/monaco-json-transform](./javascript/monaco-json-transform)| Monaco editor extension for JSON transformers |MIT|![npm](https://img.shields.io/npm/v/@nlighten/monaco-json-transform)|

## Getting Started

### Java

- In your application initialization code set the JsonTransformerConfiguration with your preferred provider:

```java
	public static void main(String[] args) {
        JsonTransformerConfiguration.set(new GsonJsonTransformerConfiguration());
        // ...
    }
```

- See available adapters [here](https://github.com/nlighten-oss/json-transform/tree/main/java/json-transform/src/main/java/co/nlighten/jsontransform/adapters)

Then in the code where you want to transform JSON objects:

```java
    // 'definition' is a JSON (in your selected provider's structure) 
    // defining the spec of the transformer    
    JsonTransformer transformer = new JsonTransformer(definition);
    // 'input' - The input of the transformer (referred as '$')
    // 'additionalContext' - (optional) Map of additional inputs to refer during transformation 
    Object transformed = transformer.transform(input, additionalContext);
```

### JavaScript

```js
    // 'definition' is the spec of the transformer    
    const transformer = new JsonTransformer(definition);
    // 'input' - The input of the transformer (referred as '$')
    // 'additionalContext' - (optional) Map of additional inputs to refer during transformation 
    const result = await transformer.transform(input, additionalContext);
```