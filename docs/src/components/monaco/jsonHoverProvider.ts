import type { Monaco } from "@monaco-editor/react";
import { formatSchemaType, type TypeSchema } from "@nlighten/json-schema-utils";
import { functions, getFunctionInlineSignature, parseArgs, transformUtils } from "@nlighten/json-transform-core";

const registerHoverProvider = (
  monaco: Monaco,
  { resolveSuggestions }: { resolveSuggestions: (path: string) => Record<string, TypeSchema> },
) => {
  const NoContent: any = {};
  monaco.languages.registerHoverProvider("json", {
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
          if (!funcName || !functions.get(funcName)) return;
          const funcType = charBefore === '"' && charAfter === '"' ? "object" : "inline";
          let func = functions.get(funcName);
          if (!func) return;
          if (func.argBased) {
            const argBasedType =
              funcType === "inline"
                ? func.argBased(parseArgs(func, word.word.match(/\(([^)]*)/)?.[1] ?? ""))
                : func.argBased({}); // TODO: is there a way to get the definition?
            if (argBasedType) func = argBasedType;
          }
          const sig = getFunctionInlineSignature(funcName, func);
          return resolve({
            range: new monaco.Range(position.lineNumber, word.startColumn, position.lineNumber, word.endColumn),
            contents: [
              {
                value: `\`${sig}\`` + (func.aliasTo ? `\n (alias to \`$$${func.aliasTo})\`` : ""),
              },
              func.deprecated
                ? {
                    value: `DEPRECATED - Please use [$$${func.deprecated}](command:docs?${encodeURIComponent(
                      JSON.stringify({ func: func.deprecated, type: func }),
                    )}) instead`,
                    isTrusted: true,
                  }
                : (null as any),
              {
                value: formatSchemaType(func.outputSchema),
              },
              { value: func.description ?? "" },
              {
                value: `See docs at [${func?.aliasTo ?? funcName}](command:docs?${encodeURIComponent(
                  JSON.stringify({ func: funcName, type: func }),
                )})`,
                isTrusted: true,
              },
            ].filter(Boolean),
          });
        } else if (
          (word.word[0] === "$" && (word.word[1] === "." || word.word[1] === "[")) ||
          (word.word[0] === "#" && transformUtils.matchesAnyOfContextVariables(word.word))
        ) {
          let varType: TypeSchema | null = null;
          // try to find var type from current model suggestions
          const path = model.uri.path.replace(/\.\w+$/, "");
          const paths = resolveSuggestions(path);
          varType = paths[word.word];
          if (varType) {
            const contents: { value: string }[] = [{ value: `**${word.word}**` }, { value: formatSchemaType(varType) }];
            if (varType.description) {
              contents.push({ value: varType.description });
            }
            return resolve({
              range: new monaco.Range(position.lineNumber, word.startColumn, position.lineNumber, word.endColumn),
              contents,
            });
          }
        }
        return resolve(NoContent);
      }),
  });
};

export default registerHoverProvider;
