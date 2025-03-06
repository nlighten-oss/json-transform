import type { languages, IRange, editor } from "monaco-editor";
import {
  ContextVariablesSchemas,
  functionsParser,
  getFunctionInlineSignature,
  getFunctionObjectSignature,
} from "@nlighten/json-transform-core";
import { formatSchemaType, type TypeSchema } from "@nlighten/json-schema-utils";

type TypeMap = Record<string, TypeSchema>;

export type JsonTransformItemCompletionProviderFactoryOptions = {
  /**
   * Get a map from json-path to its type schema
   * @param model The model the type map belongs to
   */
  getTypeMap?: (model: editor.ITextModel) => TypeMap | undefined;

  /**
   * Get a list of suggestions (e.g. variable names/paths) for the current model
   * @param model
   */
  getSuggestions?: (model: editor.ITextModel) => string[] | undefined;
};

export const jsonTransformerItemCompletionProvider: (
  options: JsonTransformItemCompletionProviderFactoryOptions,
) => languages.CompletionItemProvider = ({ getTypeMap, getSuggestions } = {}) => {
  const jsonVariableMapper = (
    name: string,
    typeMap: TypeMap | undefined,
    range: IRange,
    inline: boolean,
  ): languages.CompletionItem => {
    let label = name[0] === "<" ? name.replace(/^<[^>]*>/, "") : name;
    let insertText: string = label.replace(/"/g, '\\"');
    let insertTextRules: any = undefined;
    let documentation: string | undefined = undefined;
    let detail = typeMap?.[name]
      ? `(${typeMap[name].type}${typeMap[name].format ? ":" + typeMap[name].format : ""})${
          typeMap[name].description ? " " + typeMap[name].description : ""
        }`
      : undefined;
    let kind: languages.CompletionItem["kind"] = 3; /* monaco.languages.CompletionItemKind.Field */
    let tags: languages.CompletionItem["tags"] | undefined = undefined;
    if (inline && name[0] === "$" && name[1] === "$") {
      const inlineFunction = functionsParser.get(name.substring(2));
      if (inlineFunction) {
        if (inlineFunction.deprecatedInFavorOf) {
          tags = [1 /* monaco.languages.CompletionItemTag.Deprecated */];
        }
        let counter = 1;
        label = name + " (inline)";

        insertText = getFunctionInlineSignature(name.substring(2), inlineFunction, true).replace("$$", "\\$\\$"); // escape the first $$
        if (insertText.includes("{")) {
          insertText = insertText.replace(/{/g, () => `$\{${counter++}:`);
        }
        insertText += `:$\{${counter}:input}`;
        insertTextRules = 4 /* monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet */;
        kind = 1 /* monaco.languages.CompletionItemKind.Function */;
        detail = formatSchemaType(inlineFunction.outputSchema);
        documentation = inlineFunction.description;
      }
    } else if (!inline && name[0] === "$" && name[1] === "$") {
      const objectFunction = functionsParser.get(name.substring(2));
      if (objectFunction) {
        let counter = 1;
        label = name + " (object)";
        insertText = getFunctionObjectSignature(name.substring(2), objectFunction);
        insertText = insertText.replace(/\{\w+}/g, m => `$\{${counter++}:"${m.substring(1)}"`);
        insertTextRules = 4 /* monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet */;
        kind = 8 /* monaco.languages.CompletionItemKind.Module */;
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
  const functionSuggestions = Array.from(functionsParser.getNames()).map(x => `$$${x}`);
  const contextSuggestions = Object.keys(ContextVariablesSchemas);

  return {
    provideCompletionItems: (model, position) => {
      //const path = model.uri.path.replace(/\.\w+$/, "");
      const typeMap = getTypeMap ? getTypeMap(model) : undefined;
      const suggestions = getSuggestions ? getSuggestions(model) : undefined;
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
        suggestions: (suggestions ?? [])
          .concat(functionSuggestions, contextSuggestions)
          .map(s => jsonVariableMapper(s, typeMap, range, inline)),
      };
    },
  };
};

/**
 * Add item completion for JSON transformers
 * Additional suggestions and type mapping can be provided in options
 * @param monaco
 * @param options
 */
export const registerJsonTransformItemCompletionProvider = (
  monaco: { languages: typeof languages },
  options: JsonTransformItemCompletionProviderFactoryOptions,
) => {
  monaco.languages.registerCompletionItemProvider("json", jsonTransformerItemCompletionProvider(options));
};
