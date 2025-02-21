import { loader, type Monaco } from "@monaco-editor/react";
import * as monaco from 'monaco-editor';
import type { IRange, languages } from "monaco-editor";
import { functionsParser, getFunctionInlineSignature, getFunctionObjectSignature } from "@nlighten/json-transform-core";
import {formatSchemaType} from "@nlighten/json-schema-utils";
import jsonVariablesTokensProvider from "./jsonVariablesTokensProvider";
import registerHoverProvider from "./jsonHoverProvider";
import {getSuggestions, resolveSuggestions} from "./suggestionsProvider";

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
  jsonVariablesTokensProvider(monaco);

  // add suggestions
  const jsonVariableMapper = (v: any, paths: any, range: IRange, inline: boolean): languages.CompletionItem => {
    let label = v[0] === "<" ? v.replace(/^<[^>]*>/, "") : v;
    let insertText: string = label.replace(/"/g, '\\"');
    let insertTextRules: any = undefined;
    let documentation: string | undefined = undefined;
    let detail = paths?.[v]
      ? `(${paths[v].type}${paths[v].format ? ":" + paths[v].format : ""})${
          paths[v].description ? " " + paths[v].description : ""
        }`
      : undefined;
    let kind = monaco.languages.CompletionItemKind.Field;
    let tags: any = undefined;
    if (inline && v[0] === "$" && v[1] === "$") {
      const inlineFunction = functionsParser.get(v.substring(2));
      if (inlineFunction) {
        if (inlineFunction.deprecated) {
          tags = [monaco.languages.CompletionItemTag.Deprecated];
        }
        let counter = 1;
        label = v + " (inline)";

        insertText = getFunctionInlineSignature(v.substring(2), inlineFunction, true).replace("$$", "\\$\\$"); // escape the first $$
        if (insertText.includes("{")) {
          insertText = insertText.replace(/{/g, () => `$\{${counter++}:`);
        }
        insertText += `:$\{${counter}:input}`;
        insertTextRules = monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet;
        kind = monaco.languages.CompletionItemKind.Function;
        documentation = inlineFunction.description;
      }
    } else if (!inline && v[0] === "$" && v[1] === "$") {
      const objectFunction = functionsParser.get(v.substring(2));
      if (objectFunction) {
        let counter = 1;
        label = v + " (object)";
        insertText = getFunctionObjectSignature(v.substring(2), objectFunction);
        insertText = insertText.replace(/\{\w+}/g, m => `$\{${counter++}:"${m.substring(1)}"`);
        insertTextRules = monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet;
        kind = monaco.languages.CompletionItemKind.Module;
        detail = objectFunction.outputSchema
          ? formatSchemaType(objectFunction.outputSchema)
          : objectFunction.description;
        documentation = objectFunction.description;
      }
    }
    return {
      label,
      kind,
      insertText,
      insertTextRules,
      range,
      detail,
      documentation,
      tags,
    };
  };
  const functionSuggestions = functionsParser.getNames()
    .filter(x => !functionsParser.get(x).deprecated)
    .map(x => `$$${x}`);

  monaco.languages.registerCompletionItemProvider("json", {
    provideCompletionItems: (model, position) => {
      const path = model.uri.path.replace(/\.\w+$/, "");
      const [suggestions, paths] = getSuggestions(path);
      const word = model.getWordUntilPosition(position);
      const range = {
        startLineNumber: position.lineNumber,
        endLineNumber: position.lineNumber,
        startColumn: word.startColumn,
        endColumn: word.endColumn,
      };
      // find out if we are inside a string or after a key inside an object
      // we count (") before position on same line
      // if even number then we are not in a string, otherwise a string (this is not precise, could have been escaped)
      const quotesCount =
        model
          .getLineContent(position.lineNumber)
          .substring(0, position.column - 1)
          .match(/^"|[^\\]"/g)?.length ?? 0; // ignore escaped double quotes
      const inline = quotesCount % 2 !== 0; // if inside a string, suggest inline functions otherwise object functions
      return {
        suggestions: suggestions.concat(functionSuggestions).map(s => jsonVariableMapper(s, paths, range, inline)),
      };
    },
  });

  monaco.editor.registerCommand("docs", function (accessor: any, arg: any) {
    window.open(functionsParser.resolveDocsUrl(arg.func, arg.type), "_blank");
  });

  registerHoverProvider(monaco, { resolveSuggestions });

  monaco.editor.defineTheme("vs-dark-custom", {
    base: "vs-dark",
    colors: {},
    inherit: true,
    rules: [
      { token: "variable", foreground: "#80ff80".substring(1) },
      { token: "variable_deprecated", foreground: "#80ff80".substring(1), fontStyle: "strikethrough" },
      { token: "member", foreground: "#30ffee".substring(1) },
      { token: "context", foreground: "#d7a6ff".substring(1) },
      { token: "annotation", foreground: "#ff8080".substring(1) },
      { token: "function", foreground: "#ffff80".substring(1) },
      { token: "function_context", foreground: "#7d7df3".substring(1) },
      { token: "function_deprecated", foreground: "#ffff80".substring(1), fontStyle: "strikethrough" },
    ],
  });
};

loader.config({ monaco })

loader.init().then(monaco => {
  glob.monaco = monaco;

  initMonaco(monaco);
  return monaco;
});
