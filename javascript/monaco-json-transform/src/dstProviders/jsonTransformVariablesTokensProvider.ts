import { languages } from "monaco-editor";
import tokenizeLine, { type TokenizationState, type Token } from "./tokenizeLine";

const LEGEND = {
  tokenTypes: [
    "variable",
    "variable_deprecated",
    "member",
    "context",
    "annotation",
    "function",
    "function_context",
    "function_deprecated",
    "comment",
    "string",
    "number",
    "keyword",
    "no_style",
  ],
  tokenModifiers: ["declaration"],
};

const tokenSorter = (a: Token, b: Token) => {
  return a.line - b.line || a.char - b.char;
};

export const jsonTransformDSTProvider: languages.DocumentSemanticTokensProvider = {
  getLegend: () => LEGEND,
  provideDocumentSemanticTokens: function (model /*, lastResultId, token*/) {
    const lines = model.getLinesContent();
    const ts: TokenizationState = { tokens: [] };

    for (let i = 0; i < lines.length; i++) {
      const line = lines[i];
      tokenizeLine(line, i, ts);
    }
    ts.tokens.sort(tokenSorter);
    const newData: any = [];
    let prevLine = 0,
      prevChar = 0;
    ts.tokens.forEach(t => {
      const deltaLine = t.line - prevLine, // translate line to deltaLine
        deltaStart = prevLine === t.line ? t.char - prevChar : t.char;
      newData.push(deltaLine, deltaStart, t.length, t.type, t.modifier);
      prevLine = t.line;
      prevChar = t.char;
    });
    return {
      data: new Uint32Array(newData), //ts.data),
      resultId: undefined,
    };
  },
  releaseDocumentSemanticTokens: (/*resultId*/) => {},
};

/**
 * Add syntax highlighting in JSON transformers (e.g `$.variable`, `$$date:...`, etc)
 */
export const registerJsonTransformDSTProvider = (monaco: { languages: typeof languages }) => {
  monaco.languages.registerDocumentSemanticTokensProvider("json", jsonTransformDSTProvider);
};
