import type { editor } from "monaco-editor";

export const vsDarkCustom: editor.IStandaloneThemeData = {
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
};

export function defineThemeVsDarkCustom(monaco: { editor: typeof editor }, name = "vs-dark-custom") {
  monaco.editor.defineTheme(name, vsDarkCustom);
}
