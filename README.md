# json-transform

- [Documentation](https://nlighten-oss.github.io/json-transform/)

## Packages

| Language   |Name| Description                                             |License|Status|
|------------|---|---------------------------------------------------------|---|---|
| Java       |[co.nlighten.json-transform](./java/json-transform)| Java library for transforming JSON objects              |Apache License 2.0|![Maven Central Version](https://img.shields.io/maven-central/v/co.nlighten/json-transform)|
| JavaScript |[@nlighten/json-transform](./javascript/json-transform)| JSON transformers JavaScript implementation|MIT|![npm](https://img.shields.io/npm/v/@nlighten/json-transform)|
| JavaScript |[@nlighten/json-transform-core](./javascript/json-transform-core)| Core types and utilities for handling JSON transformers |MIT|![npm](https://img.shields.io/npm/v/@nlighten/json-transform-core)|

## Getting Started

### Java

- In your application initialization code set the JsonTransformerConfiguration with your preferred provider:

```java
	public static void main(String[] args) {
        JsonTransformerConfiguration.set(new GsonJsonTransformerConfiguration());
        // ...
    }
```

Then in the code where you want to transform JSON objects:

```java
    // 'definition' is a JSON (in your selected provider's structure) 
    // defining the spec of the transformer    
    JsonTransformer transformer = new JsonTransformer(definition);
    // 'input' - The input of the transformer (referred as '$')
    // 'additionalContext' - (optional) Map of additional inputs to refer during transformation 
    Object transformed = transformer.transform(input, additionalContext);
```