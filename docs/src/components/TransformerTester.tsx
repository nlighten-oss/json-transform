import React, {useEffect, useMemo, useState} from "react";
import  { } from "@docusaurus/BrowserOnly"
import { JSONSchemaUtils } from '@nlighten/json-schema-utils'
import {jsonpathJoin} from "@nlighten/json-transform-core";
import {DebuggableTransformerFunctions, JsonTransformer} from "@nlighten/json-transform";
import {useLocation, useHistory} from "@docusaurus/router";
import MonacoEditor from "./monaco/MonacoEditor";
import {setSuggestions} from "./monaco/suggestionsProvider";
import useJSONString from "./hooks/useJSONString";
import JSONGzip from "../utils/JSONGzip";
import MarkmapView from "./markmap/MarkmapView";
import transformerToMarkmap from "./transformerToMarkmap";
import Modal from "./Modal";

const DEFAULT_INPUT_VALUE = `{
  "first_name": "John",
  "last_name": "Doe",
  "date_of_birth": "1980-01-01"
}`,
  DEFAULT_DEFINITION_VALUE = `{
    "*": "$",
    "full_name": {
      "$$join": ["$.first_name", "$.last_name"],
      "delimiter": " "
    },
    "age": {
      "$$math": [ 
        {
          "$$math": [
            "$$date(EPOCH):#now",
            "-",
            "$$date(EPOCH):$.date_of_birth"
          ]
        },
        "//",
        "$$math(365,*,'$$math(24,*,3600)')"
      ]
    }
}`;

const transformAsync = async (input: any, definition: any) => {
  const adapter = new DebuggableTransformerFunctions();
  const transformer = new JsonTransformer(definition, adapter);
  const result = await transformer.transform(input);
  return {
    result,
    debugInfo: adapter.getDebugResults()
  };
}

const narrowScreen = window.innerWidth < 996;
const initialHeight = narrowScreen ? 280 : 450;

export const SHARE_QS = "shared";
export const GZIP_MARKER = "gzip,";

const TransformerTester = () => {
  const [inputString, setInputString, parsedInput, inputError] = useJSONString(DEFAULT_INPUT_VALUE);
  const [transformerString, setTransformerString, parsedTransformer, transformerError] =
    useJSONString(DEFAULT_DEFINITION_VALUE);
  const [outputJSONString, setOutputJSONString] = useState("");
  const [outputError, setOutputError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false)

  const location = useLocation()
  const history = useHistory()

  useEffect(() => {
    if (parsedInput) {
      const generatedSchema = JSONSchemaUtils.generate(parsedInput);
      const result = JSONSchemaUtils.parse(generatedSchema);
      setSuggestions("/transformer",
        ["#now", "#null", "#uuid"].concat(
          result?.paths.map(x => jsonpathJoin("$", x.$path)) ?? []
        ),
        result?.paths.reduce((a, c) => {
          a[jsonpathJoin("$", c.$path)] = c;
          return a;
        }, {} as any),)
    } else {
      setSuggestions("/transformer", ["#now", "#null", "#uuid"], {});
    }
  }, [parsedInput]);

  const handleTransform = () => {
    setLoading(true);
    transformAsync(parsedInput, parsedTransformer)
      .then(data => {
        setOutputJSONString(JSON.stringify(data.result, null, 2))
        console.log("debug-info:", data.debugInfo)
      })
      .then(() => setOutputError(null))
      .catch(e => setOutputError(e.message))
      .finally(() => setLoading(false));
  }

  useEffect(() => {
    const searchIndex = location.hash?.indexOf("?");
    if (searchIndex === -1 || !location.hash.includes(SHARE_QS + "=", searchIndex)) return;
    const sp = new URLSearchParams(location.hash.substring(searchIndex + 1));
    if (!sp.has(SHARE_QS)) return;
    const sharedValue = sp.get(SHARE_QS);
    if (!sharedValue) return;
    try {
      (sharedValue.startsWith(GZIP_MARKER)
          ? JSONGzip.decompress(sharedValue.substring(GZIP_MARKER.length), "base64url")
          : Promise.resolve(JSON.parse(sharedValue))
      )
        .then(defaults => {
          setInputString(JSON.stringify(defaults.i, null, 2));
          setTransformerString(JSON.stringify(defaults.d, null, 2));
          history.replace("#");
        })
        .catch(e => {
          console.error(e);
          history.replace("#");
        });
    } catch (e: any) {
      console.error(e);
      history.replace("#");
    }
  }, [location.hash, history.replace]);

  const handleShare = async () => {
    const input = JSON.stringify({i: parsedInput, d: parsedTransformer});
    const compressed = await JSONGzip.compress(input, "base64url", "string");
    const encodedInput = encodeURIComponent(input);
    const sharedValue = encodedInput.length > compressed.length + GZIP_MARKER.length
      ? GZIP_MARKER + compressed // compressed is better
      : encodedInput;

    prompt("Copy this value and share it", window.location.origin + window.location.pathname + `#?${SHARE_QS}=` + sharedValue);
  };

  const handleVisualize = ()=> {
    setModalOpen(true)
  }

  const markmapRoot = useMemo(() => {
    return transformerToMarkmap(parsedTransformer, "output");
  }, [parsedTransformer]);

  return (
    <>
      <div className="tester_grid">
        <h3 className="hide-on-mobile">Input</h3>
        <h3 style={{display: "flex", flexDirection: "row-reverse", justifyContent: "space-between"}}>
          <span className="hide-on-mobile">&nbsp;</span>
          <button type="button" className="button button--primary button--sm share-button shadow--lw"
                  disabled={!parsedTransformer}
                  onClick={handleVisualize}>
            <span style={{display: 'flex', alignItems: "center", gap: "4px"}}>
              <svg focusable="false" viewBox="0 0 24 24" width={16} height={16} fill="currentcolor">
                <path d="M22 11V3h-7v3H9V3H2v8h7V8h2v10h4v3h7v-8h-7v3h-2V8h2v3z"></path>
              </svg>
              Visualize Transformer
            </span>
          </button>
          <span className="hide-on-mobile">Transformer</span>
        </h3>
        <h3 style={{display: "flex", flexDirection: "row-reverse", justifyContent: "space-between"}}>
          <button type="button" className="button button--primary button--sm share-button shadow--lw"
                  disabled={!parsedTransformer}
                  onClick={handleShare}>
            <span style={{display: 'flex', alignItems: "center", gap: "4px"}}>
              <svg focusable="false" viewBox="0 0 24 24" width={16} height={16} fill="currentcolor">
                <path
                  d="M18 16.08c-.76 0-1.44.3-1.96.77L8.91 12.7c.05-.23.09-.46.09-.7s-.04-.47-.09-.7l7.05-4.11c.54.5 1.25.81 2.04.81 1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3c0 .24.04.47.09.7L8.04 9.81C7.5 9.31 6.79 9 6 9c-1.66 0-3 1.34-3 3s1.34 3 3 3c.79 0 1.5-.31 2.04-.81l7.12 4.16c-.05.21-.08.43-.08.65 0 1.61 1.31 2.92 2.92 2.92s2.92-1.31 2.92-2.92-1.31-2.92-2.92-2.92"></path>
              </svg>
              Share Input and Transformer
            </span>
          </button>
          <span className="hide-on-mobile">Output</span>
        </h3>
        <MonacoEditor language="json" value={inputString} onChange={(value) => setInputString(value)}
                      height={initialHeight}/>
        <MonacoEditor language="json" model="transformer.json"
                      value={transformerString}
                      validateVariables
                      onChange={(value) => setTransformerString(value)}
                      height={initialHeight}/>
        <MonacoEditor language="json" readOnly value={outputJSONString} height={initialHeight}/>
        <div style={{color: 'red', fontSize: "0.7em"}}>{inputError?.message}</div>
        <div className="button-container">
          <div style={{color: 'red', fontSize: "0.7em"}}>{transformerError?.message}</div>
          <button type="button" className="button button--primary transform-button shadow--lw"
                  disabled={loading || !!(inputError || transformerError)}
                  onClick={handleTransform}>Transform
          </button>
          <div data-loader className={loading ? undefined : "hide"}></div>
        </div>
        <div style={
          outputError
            ? {color: 'red', fontSize: "0.7em"}
            : outputJSONString
              ? {fontSize: "0.7em"}
              : undefined
        }>
          {outputError ??
            (outputJSONString
              ? "* You can open the browser console to see debug information about the transformation"
              : undefined)
          }
        </div>
      </div>
      {modalOpen && <Modal title="Visualize Transformer" onRequestClose={() => setModalOpen(false)}>
        <MarkmapView style={{
          "--markmap-font": "300 14px/16px monospace",
          "--markmap-text-color": "currentcolor",
          "--markmap-circle-open-bg":"var(--ifm-background-surface-color)",
          "--markmap-keyword-color":"var(--ifm-color-primary)",
          width: "90vw",
          height: "90vh"
        } as any} root={markmapRoot} />
      </Modal>}
    </>
  );
}

export default TransformerTester;