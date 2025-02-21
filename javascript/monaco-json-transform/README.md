# monaco-json-transform

[Monaco Editor](https://github.com/microsoft/monaco-editor) JSON Transform language tokenizer (and syntax highlight), hover provider and more.

## Installation

- `npm i -S @nlighten/monaco-json-transform`

## Usage

With a helper function to register straight to monaco
```js
// `monaco` should be global or local in the loading module

import { registerJQLanguageDefinition } from 'monaco-languages-json-transform';

...

registerJQLanguageDefinition(monaco);
```

DIY (in case you want it to be called some other name):
```js
// `monaco` should be global or local in the loading module

import { JQLanguageDefinition } from 'monaco-languages-json-transform';

...

// Register a new language
monaco.languages.register({ id: 'jq' });

// Register a tokens provider for the language
monaco.languages.setMonarchTokensProvider('jq', JQLanguageDefinition);
```

In monaco editor use `"jq"` (or your value if changed) as `language`.

## License

monaco-languages-json-transform is [MIT Licensed](https://github.com/elisherer/monaco-languages-json-transform/blob/master/LICENSE)
