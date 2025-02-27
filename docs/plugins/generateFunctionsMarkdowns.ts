import fs from "node:fs";
import path from "node:path";
import { definitions } from "@nlighten/json-transform-core";

export function generateFunctionsMarkdowns() {
  const functionsDir = path.join(__dirname, "..", "docs", "functions");

  for (const functionName in definitions) {
    const definition = definitions[functionName];

    let subSections = "";
    if (!definition) {
      throw new Error("not found function " + functionName);
    }
    if (definition.subfunctions) {
      for (let i = 0; i < definition.subfunctions.length; i++) {
        const subDef = definition.subfunctions[i];
        subSections += `### ${
          subDef.if.length === 1
            ? (subDef.if[0].equals === "TRUE" ? "" : subDef.if[0].equals) + " " + subDef.if[0].argument
            : `When ${subDef.if.map(c => `${c.argument} = ${c.equals}`).join(" and ")}`
        }

<FunctionIntro sub={${i}} />

#### Usage

<FunctionUsage sub={${i}} />

#### Returns

<FunctionReturns sub={${i}} />

#### Arguments

<FunctionArguments sub={${i}} />

`;
      }
    }

    const functionMarkdown = `
<FunctionIntro />

### Usage

<FunctionUsage />

### Returns

<FunctionReturns />

### Arguments

<FunctionArguments />

${subSections}

### Examples

<FunctionExamples />
`;

    fs.writeFileSync(path.join(functionsDir, `${functionName}.md`), functionMarkdown);
  }
}
