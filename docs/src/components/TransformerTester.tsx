import React, {useEffect, useState} from "react";
import { JSONSchemaUtils } from '@nlighten/json-schema-utils'
import MonacoEditor from "../components/monaco/MonacoEditor";
import useJSONString from "../components/hooks/useJSONString";
import {setSuggestions} from "@site/src/components/monaco/suggestionsProvider";
import {jsonpathJoin} from "@nlighten/json-transform-core";
import {JsonTransformer} from "@nlighten/json-transform";

const DEFAULT_INPUT_VALUE = `{
  "first_name": "John",
  "last_name": "Doe",
  "date_of_birth": "1980-01-01"
}`,
  DEFAULT_DEFINITION_VALUE = `{
    "*": "$",
    "full_name": {
      "$$join": ["$.first_name", " ", "$.last_name"]
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
        "$$math(365,*,86400)"
      ]
    }
}`;

const transformAsync = async (input: any, definition: any) => {
  const adapter = JsonTransformer.getDebuggableAdapter();
  const transformer = new JsonTransformer(definition, adapter);
  const result = await transformer.transform(input);
  return {
    result,
    debugInfo: adapter.getDebugResults()
  };
}

const narrowScreen = window.innerWidth < 996;
const initialHeight = narrowScreen ? 280 : 450;

const TransformerTester = () => {
  const [inputString, setInputString, parsedInput, inputError] = useJSONString(DEFAULT_INPUT_VALUE);
  const [transformerString, setTransformerString, parsedTransformer, transformerError] =
    useJSONString(DEFAULT_DEFINITION_VALUE);
  const [outputJSONString, setOutputJSONString] = useState("");
  const [outputError, setOutputError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

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

  return <div className="tester_grid">
    <h3 className="desktop-title">Input</h3>
    <h3 className="desktop-title">Transformer</h3>
    <h3 className="desktop-title">Output</h3>
    <MonacoEditor language="json" value={inputString} onChange={(value) => setInputString(value)} height={initialHeight}/>
    <MonacoEditor language="json" model="transformer.json"
                  value={transformerString}
                  validateVariables
                  onChange={(value) => setTransformerString(value)}
                  height={initialHeight}/>
    <MonacoEditor language="json" readOnly value={outputJSONString} height={initialHeight}/>
    <div style={{color: 'red'}}>{inputError?.message}</div>
    <div className="button-container">
      <div style={{color: 'red'}}>{transformerError?.message}</div>
      <button type="button" className="button button--primary transform-button shadow--lw"
              disabled={loading || !!(inputError || transformerError)}
              onClick={handleTransform}>Transform
      </button>
      <div data-loader className={loading ? undefined:"hide" }></div>
    </div>
    <div style={outputError ? {color: 'red'} : outputJSONString ? { fontSize: "0.7em"} : undefined}>{outputError ?? (outputJSONString ? "* You can open the browser console to see debug information about the transformation" : undefined)}</div>
  </div>
}

export default TransformerTester;