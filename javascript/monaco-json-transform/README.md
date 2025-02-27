# monaco-json-transform

[Monaco Editor](https://github.com/microsoft/monaco-editor) JSON Transform language tokenizer (and syntax highlight), hover provider and more.

## Installation

- `npm i -S @nlighten/monaco-json-transform`

## Usage

With a helper function to register straight to monaco
```js
// `monaco` should be global or local in the loading module

import {
  registerJsonTransformItemCompletionProvider,
  registerJsonTransformDSTProvider,
  registerJsonTransformHoverProvider,
  defineThemeVsDarkCustom,
} from "@nlighten/monaco-json-transform";

...

registerJsonTransformDSTProvider(monaco);

registerJsonTransformItemCompletionProvider(monaco, {
  getTypeMap: model => { // example
    const path = model.uri.path;
    const [, paths] = getSuggestions(path);
    return paths;
  },
  getSuggestions: model => { // example
    const path = model.uri.path;
    return getSuggestions(path)[0];
  },
});

registerJsonTransformHoverProvider(monaco, {
  getTypeMap: model => { // example
    const path = model.uri.path;
    return getSuggestions(path, true)[1];
  },
});

defineThemeVsDarkCustom(monaco);
```


For syntax highlighting, in monaco editor, use `"vs-dark-custom"` (or your value if changed) as `theme`.

## License

monaco-json-transform is [MIT Licensed](https://github.com/elisherer/monaco-languages-json-transform/blob/master/LICENSE)
