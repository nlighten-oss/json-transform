import * as monaco from "monaco-editor";
import { loader, type Monaco } from "@monaco-editor/react";
import { getSuggestions } from "./suggestionsProvider";

import {
  registerJsonTransformItemCompletionProvider,
  registerJsonTransformDSTProvider,
  registerJsonTransformHoverProvider,
  defineThemeVsDarkCustom,
} from "@nlighten/monaco-json-transform";

const glob = window as unknown as { monaco: Monaco };

export const getMonaco = (): Monaco => {
  return glob.monaco;
};

export const getModel = (value: string, uri: string, language = "json", path?: string | undefined) => {
  const monaco = getMonaco();
  if (!monaco) {
    console.warn("Called getModel before it is available!");
    return null;
  }
  const monacoUri = monaco.Uri.parse(path || uri);
  const existingModel = glob.monaco.editor.getModels().find(m => m.uri.toString() === monacoUri.toString());
  if (existingModel) {
    existingModel.applyEdits([
      {
        range: existingModel.getFullModelRange(),
        text: value,
      },
    ]);
  }

  return existingModel || monaco.editor.createModel(value, language, monacoUri);
};

const initMonaco = (monaco: Monaco) => {
  registerJsonTransformDSTProvider(monaco);

  registerJsonTransformItemCompletionProvider(monaco, {
    getTypeMap: model => {
      const path = model.uri.path.replace(/\.\w+$/, "");
      const [, paths] = getSuggestions(path);
      return paths;
    },
    getSuggestions: model => {
      const path = model.uri.path.replace(/\.\w+$/, "");
      return getSuggestions(path)[0];
    },
  });

  registerJsonTransformHoverProvider(monaco, {
    getTypeMap: model => {
      const path = model.uri.path.replace(/\.\w+$/, "");
      console.log("looking for: " + path);
      return getSuggestions(path, true)[1];
    },
  });

  defineThemeVsDarkCustom(monaco);
};

loader.config({ monaco });

loader.init().then(monaco => {
  glob.monaco = monaco;

  initMonaco(monaco);
  return monaco;
});
