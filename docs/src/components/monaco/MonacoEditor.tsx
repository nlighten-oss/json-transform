import { useCallback, useEffect, useRef, useMemo, useState } from "react";
import type { editor, Position } from "monaco-editor";
import Editor, { EditorProps, OnChange, OnMount } from "@monaco-editor/react";
import { isValidPropertyPath, type TypeSchema } from "@nlighten/json-schema-utils";
import { transformUtils } from "@nlighten/json-transform-core";
import { getModel, getMonaco } from "./Monaco.init";
import { getSuggestions } from "./suggestionsProvider";

const glob = window as any;

const LoaderStyles = {
  display: "flex",
  color: "#858585",
  backgroundColor: "#1e1e1e",
  width: "100%",
  height: "100%",
  justifyContent: "center",
  alignItems: "center",
};

export enum MonacoEditorOptionsVariant {
  FULL,
  DEFAULT,
  HIGHLIGHT,
}

// https://microsoft.github.io/monaco-editor/api/interfaces/monaco.editor.ieditorconstructionoptions.html
const MonacoOptions: Record<MonacoEditorOptionsVariant, editor.IStandaloneEditorConstructionOptions> = {
  [MonacoEditorOptionsVariant.FULL]: {
    tabSize: 2,
    quickSuggestions: true,
    suggest: {
      snippetsPreventQuickSuggestions: false,
    },
    showFoldingControls: "always",
    //scrollBeyondLastLine: false,
    scrollbar: {
      horizontal: "visible",
      horizontalScrollbarSize: 27,
    },
    "semanticHighlighting.enabled": true,
  },
  [MonacoEditorOptionsVariant.DEFAULT]: {
    tabSize: 2,
    quickSuggestions: true,
    suggest: {
      snippetsPreventQuickSuggestions: false,
      preview: true,
    },
    scrollBeyondLastLine: false,
    minimap: { enabled: false },
    links: false,
    "semanticHighlighting.enabled": true,
  },
  [MonacoEditorOptionsVariant.HIGHLIGHT]: {
    tabSize: 2,
    scrollBeyondLastLine: false,
    // hide right side
    minimap: { enabled: false },
    overviewRulerBorder: false,
    overviewRulerLanes: 0,
    hideCursorInOverviewRuler: true,
    renderLineHighlight: "none",
    foldingHighlight: false,
    selectionHighlight: false,
    occurrencesHighlight: "off",
    // hide left side
    lineNumbers: "off",
    glyphMargin: false,
    folding: false,
    // Undocumented see https://github.com/Microsoft/vscode/issues/30795#issuecomment-410998882
    lineDecorationsWidth: 10,
    lineNumbersMinChars: 0,
    links: false,
    wordWrap: "on",
    "semanticHighlighting.enabled": true,
  },
};

export type MonacoEditorProps = {
  variant?: MonacoEditorOptionsVariant;
  onTextChange?: (newValue: string) => any;
  resizeProp?: any;
  delayResize?: number | boolean;
  model?: string;
  readOnly?: boolean;
  onDidChangeCursorPosition?: (event: any) => any;
  onDidAttemptReadOnlyEdit?: (event: any) => any;
  onMarkersChanged?: (markers: editor.IMarker[]) => any;
  initialPosition?: Position;
  makeGlobal?: boolean;
  validateVariables?: boolean;
};

export const getPathsFromModel = (model: string): [Record<string, TypeSchema>, string[]] => {
  const path = model.replace(/(__.*__)?\.\w+$/, "");
  const [suggestions, paths] = getSuggestions("/" + path);
  return [paths, suggestions];
};

const MonacoEditor = ({
  variant = MonacoEditorOptionsVariant.DEFAULT,
  onTextChange,
  resizeProp,
  delayResize = 300,
  model,
  readOnly,
  onDidChangeCursorPosition,
  onDidAttemptReadOnlyEdit,
  onMarkersChanged,
  initialPosition,
  makeGlobal,
  validateVariables,
  ...rest
}: MonacoEditorProps & EditorProps) => {
  const [mounted, setMounted] = useState(false);
  const editorRef = useRef<editor.IStandaloneCodeEditor>();
  const handleChange = useCallback<OnChange>(
    value => {
      const outValue = value?.replace(/\r\n/g, "\n");
      if (onTextChange) onTextChange(outValue ?? "");
      return outValue;
    },
    [onTextChange],
  );

  useEffect(() => {
    if (!validateVariables || !mounted || !model || !rest.value) return;

    const _monaco = getMonaco();
    const m = editorRef.current?.getModel();
    if (!m) return;

    const [paths, s] = getPathsFromModel(model);
    const markers: editor.IMarkerData[] = [];
    const lines = m.getLinesContent();
    if (!rest.language || rest.language === "json") {
      for (let i = 0; i < lines.length; i++) {
        const line = lines[i];

        const iter = line.matchAll(transformUtils.variableDetectionRegExp);
        for (
          let iterResult = iter.next(), match: RegExpMatchArray | null = iterResult.value;
          !iterResult.done;
          iterResult = iter.next(), match = iterResult.value as RegExpMatchArray | null
        ) {
          if (!match || typeof match.index === "undefined") continue;
          const sVar = match[0]
            .replace(/\["([^"]+)"]/g, ".$1")
            .replace(/\[\\"([^"]+)\\"]/g, ".$1") // double quotes escaped
            .replace(/\['([^']+)']/g, ".$1")
            .replace(/\[[^\]]+]/g, "[]"); // remove indexers
          if (
            (!match[0] && match[0] !== "") || // Skip if: no var (not empty string)
            s.includes(match[0]) ||
            s.includes(sVar) || // var exists (in simple form or original)
            /\.\.|[=@?:*]/.test(match[0]) || // var is a JSONPath query
            isValidPropertyPath(sVar, paths)
          )
            continue;

          markers.push({
            message: `Variable ${sVar} not defined in this scope`,
            severity: _monaco.MarkerSeverity.Error,
            startLineNumber: i + 1,
            startColumn: match.index + 1,
            endLineNumber: i + 1,
            endColumn: match.index + 1 + match[0].length,
          });
        }
      }
    }
    _monaco.editor.setModelMarkers(m, "nlighten", markers);
  }, [mounted, rest.value, rest.language, model, editorRef, validateVariables]);

  const didMount = useCallback<OnMount>(
    (editor, monaco) => {
      editorRef.current = editor;
      const currentValue = editor.getValue();
      if (model) {
        try {
          editor.setModel(getModel(currentValue, model, rest.language || "json", rest.path));
          //console.log("model was set to ", model);
        } catch (e) {
          console.error(e);
        }
      }
      if (onDidChangeCursorPosition) {
        editor.onDidChangeCursorPosition(onDidChangeCursorPosition);
      }
      if (onDidAttemptReadOnlyEdit) {
        editor.onDidAttemptReadOnlyEdit(() => {
          onDidAttemptReadOnlyEdit({ position: editor.getPosition() });
        });
      }
      if (initialPosition) {
        editor.setPosition(initialPosition);
        editor.focus();
      }
      makeGlobal &&
        setTimeout(() => {
          glob.__monaco_select = (loc: any) => {
            try {
              /*console.debug(
                `requested to select range (${loc.start.line + 1}, ${loc.start.column + 1}, ${loc.end.line + 1}, ${
                  loc.end.column + 1
                })`
              );*/
              const range = new monaco.Range(
                loc.start.line + 1,
                loc.start.column + 1,
                loc.end.line + 1,
                loc.end.column + 1,
              );
              editor.revealRangeAtTop(range);
              editor.setPosition({ lineNumber: loc.start.line + 1, column: loc.start.column + 1 });
              glob.__monaco_editor = editor;
            } catch (e) {
              console.error(e);
            }
          };
        }, 0);
      setMounted(true);
    },
    [
      model,
      onDidChangeCursorPosition,
      makeGlobal,
      rest.language,
      rest.path,
      onDidAttemptReadOnlyEdit,
      initialPosition,
      onMarkersChanged,
    ],
  );

  const options = useMemo(() => {
    if (readOnly) {
      return {
        ...MonacoOptions[variant],
        readOnly,
      };
    }
    return MonacoOptions[variant];
  }, [readOnly, variant]);

  useEffect(() => {
    let timer2: any;
    const handleResize = () => {
      if (!editorRef.current || typeof delayResize !== "number") return;
      editorRef.current.layout({ width: 16, height: 0 });
      timer2 = setTimeout(() => {
        if (!editorRef.current) return;
        editorRef.current.layout();
      }, delayResize);
    };

    if (typeof delayResize === "number") {
      const timer = setTimeout(handleResize, 0);
      return () => {
        clearTimeout(timer);
        clearTimeout(timer2);
      };
    }
  }, [resizeProp, delayResize, editorRef]);

  useEffect(() => {
    if (!onMarkersChanged) {
      return;
    }
    const _monaco = getMonaco();
    const disposable = _monaco.editor.onDidChangeMarkers(() => {
      const model = editorRef.current!.getModel();
      if (model) {
        onMarkersChanged(_monaco.editor.getModelMarkers({ /*owner: model.getLanguageId()*/ resource: model.uri }));
      }
    });
    return () => {
      disposable.dispose();
    };
  }, [onMarkersChanged, editorRef]);

  useEffect(() => {
    if (initialPosition && editorRef.current) {
      editorRef.current.setPosition(initialPosition);
      editorRef.current.focus();
    }
  }, [editorRef, initialPosition]);

  const loader = <div style={LoaderStyles}>Loading...</div>;

  return (
    <Editor
      theme="vs-dark-custom"
      options={options}
      onMount={didMount}
      onChange={handleChange}
      loading={loader}
      defaultLanguage={rest.language ?? undefined}
      defaultPath={model}
      path={model}
      {...rest}
    />
  );
};

export default MonacoEditor;
