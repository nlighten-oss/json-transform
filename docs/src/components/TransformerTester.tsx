import React, {useState} from "react";
import MonacoEditor from "../components/monaco/MonacoEditor";
import useJSONString from "../components/hooks/useJSONString";

const API_URL = process.env.NODE_ENV !== 'production' ? "http://localhost:10000/api/v1/transform" :
    "https://json-transform-playground-latest.onrender.com/api/v1/transform";
const DEFAULT_INPUT_VALUE = `{
    "first_name": "John",
    "last_name": "Doe",
    "date_of_birth": "1980-01-01"
}`,
    DEFAULT_DEFINITION_VALUE = `{
    "full_name": {
        "$$join": ["$.first_name", " ", "$.last_name"]
    },
    "age": {
        "$$math": [ {
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

const TransformerTester = () => {
    const [inputString, setInputString, parsedInput, inputError] = useJSONString(DEFAULT_INPUT_VALUE);
    const [transformerString, setTransformerString, parsedTransformer, transformerError] =
        useJSONString(DEFAULT_DEFINITION_VALUE);
    const [outputJSONString, setOutputJSONString] = useState("");
    const [outputError, setOutputError] = useState<string | null>(null);

    const handleTransform = () => {
        fetch(API_URL, {
          method: "POST",
            headers: {
              "Content-Type": "application/json"
            },
            body: JSON.stringify({input: parsedInput, definition: parsedTransformer}),
        })
            .then(res => res.json())
            .then(data => setOutputJSONString(JSON.stringify(data.result, null, 2)))
            .then(() => setOutputError(null))
            .catch(e => setOutputError(e.message));
    }

    return <div className="examples_grid">
        <MonacoEditor language="json" value={inputString} onChange={(value) => setInputString(value)} height={350}/>
        <MonacoEditor language="json" value={transformerString} onChange={(value) => setTransformerString(value)}
                      height={350}/>
        <MonacoEditor language="json" readOnly value={outputJSONString} height={350}/>
        <div style={{color: 'red'}}>{inputError?.message}</div>
        <div style={{textAlign: "center"}}>
            <div style={{color: 'red'}}>{transformerError?.message}</div>
            <button type="button" className="button button--primary shadow--lw"
                    disabled={!!(inputError || transformerError)}
                    onClick={handleTransform}>Transform
            </button>
        </div>
        <div style={{color: 'red'}}>{outputError}</div>
    </div>
}

export default TransformerTester;