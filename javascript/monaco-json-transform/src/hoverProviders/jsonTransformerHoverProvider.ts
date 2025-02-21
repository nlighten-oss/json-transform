import type { languages, editor } from "monaco-editor";
import { formatSchemaType, type TypeSchema } from "@nlighten/json-schema-utils";
import {
  ContextVariablesSchemas,
  functionsParser,
  getFunctionInlineSignature, getOverriddenFunction,
  parseArgs
} from "@nlighten/json-transform-core";

type TypeMap = Record<string, TypeSchema>;

const NoContent: any = {};

export type JsonTransformHoverProviderFactoryOptions = {
  /**
   * Get a map from json-path to its type schema
   * @param model The model the type map belongs to
   */
  getTypeMap?: (model: editor.ITextModel) => TypeMap;
  dontRegisterDocsCommand?: boolean;
  dontShowDocsLink?: boolean;
};

export const jsonTransformHoverProviderFactory: (
  options: JsonTransformHoverProviderFactoryOptions,
) => languages.HoverProvider = ({ getTypeMap, dontShowDocsLink } = {}) => ({
  provideHover: (model, position, token) =>
    new Promise(resolve => {
      if (token.isCancellationRequested) return resolve(NoContent);

      token.onCancellationRequested(() => {
        resolve(NoContent);
      });
      const word = model.getWordAtPosition(position);
      if (!word) return resolve(NoContent);
      if (word.word.startsWith("$$")) {
        // function documentation
        const charBefore = model.getValueInRange({
          startLineNumber: position.lineNumber,
          startColumn: word.startColumn - 1,
          endLineNumber: position.lineNumber,
          endColumn: word.startColumn,
        });
        const charAfter = model.getValueInRange({
          startLineNumber: position.lineNumber,
          startColumn: word.endColumn,
          endLineNumber: position.lineNumber,
          endColumn: word.endColumn + 1,
        });
        const funcName = word.word.match(/^\$\$([^:(]+)/)?.[1];
        if (!funcName || !functionsParser.get(funcName)) return;
        const funcType = charBefore === '"' && charAfter === '"' ? "object" : "inline";
        let func = functionsParser.get(funcName);
        if (!func) return;
        if (func.overrides && funcType === "inline") { // TODO: is there a way to get the definition for object?
          const args = parseArgs(func, word.word.match(/\(([^)]*)/)?.[1] ?? "");
          func = getOverriddenFunction(func, args);
        }
        const sig = getFunctionInlineSignature(funcName, func);
        return resolve({
          range: {
            startLineNumber: position.lineNumber,
            startColumn: word.startColumn,
            endLineNumber: position.lineNumber,
            endColumn: word.endColumn,
          },
          contents: [
            {
              value: `\`${sig}\`` + (func.aliasTo ? `\n (alias to \`$$${func.aliasTo})\`` : ""),
            },
            func.deprecatedInFavorOf && !dontShowDocsLink
              ? {
                  value: `DEPRECATED - Please use [$$${func.deprecatedInFavorOf}](command:docs?${encodeURIComponent(
                    JSON.stringify({ func: func.deprecatedInFavorOf, type: func }),
                  )}) instead`,
                  isTrusted: true,
                }
              : (null as any),
            {
              value: formatSchemaType(func.outputSchema),
            },
            { value: func.description ?? "" },
            !dontShowDocsLink && {
              value: `See docs at [${func?.aliasTo ?? funcName}](command:docs?${encodeURIComponent(
                JSON.stringify({ func: funcName, type: func }),
              )})`,
              isTrusted: true,
            },
          ].filter(Boolean),
        });
      } else {
        let varType: TypeSchema | null | undefined = null;
        // try to find var type from current model suggestions
        const typeMap = getTypeMap ? getTypeMap(model) : undefined;
        varType = typeMap?.[word.word] ?? ContextVariablesSchemas[word.word];
        if (varType) {
          const contents: { value: string }[] = [{ value: `**${word.word}**` }, { value: formatSchemaType(varType) }];
          if (varType.description) {
            contents.push({ value: varType.description });
          }
          return resolve({
            range: {
              startLineNumber: position.lineNumber,
              startColumn: word.startColumn,
              endLineNumber: position.lineNumber,
              endColumn: word.endColumn,
            },
            contents,
          });
        }
      }
      return resolve(NoContent);
    }),
});

/**
 * Add tokens hover information for JSON transformers
 * @param monaco
 * @param options
 */
export const registerJsonTransformHoverProvider = (
  monaco: { languages: typeof languages; editor: typeof editor },
  options: JsonTransformHoverProviderFactoryOptions,
) => {
  if (!options.dontRegisterDocsCommand) {
    monaco.editor.registerCommand("docs", function (accessor: any, arg: any) {
      window.open(functionsParser.resolveDocsUrl(arg.func, arg.type), "_blank");
    });
  }

  monaco.languages.registerHoverProvider("json", jsonTransformHoverProviderFactory(options));
};
