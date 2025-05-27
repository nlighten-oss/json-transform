import fs from "node:fs";
import path from "node:path";
import { definitions, examples, FunctionDefinition } from "@nlighten/json-transform-core";

const fixType = (arg: any) => {
  const result: any = {
    type: arg.type,
    description: arg.description,
    default: arg.default,
  };
  // if (typeof arg.position === "number") {
  //   result.$comment = `pos=${arg.position}`;
  // }
  if (result.type === "long") result.type = "integer";
  else if (result.type === "BigDecimal") result.type = "number";
  else if (result.type === "string[]") {
    result.type = "array";
    result.items = { type: "string" };
  } else if (result.type === "enum") {
    result.type = "string";
    if (arg.enum && arg.enum.length) {
      result.enum = arg.enum;
    }
  } else if (result.type === "const") {
    result.type = typeof arg.const;
    result.const = arg.const;
  } else if (result.type === "transformer" || result.type === "any") {
    delete result.type;
    result.anyOf = [
      { type: "object" },
      { type: "string" },
      { type: "array" },
      { type: "number" },
      { type: "integer" },
      { type: "boolean" },
      { type: "null" },
    ];
  }

  return result;
};

const argumentsAsInputSchema = (func: FunctionDefinition) => {
  let result = { type: "array", minLength: 0, maxLength: 0, items: [] as any[] };
  func.arguments
    ?.slice()
    .sort((a, b) => (a.position ?? Infinity) - (b.position ?? Infinity))
    .forEach((arg: any) => {
      result.maxLength++;
      if (arg.required) {
        result.minLength++;
      }
      result.items.push(fixType(arg));
    });
};

export default function () {
  const language: any = {};
  // add all markdown files
  const docsDir = path.join(__dirname, "..", "..", "docs");
  fs.readdirSync(docsDir)
    .filter(f => f.endsWith(".md"))
    .forEach(filename => {
      let doc = fs.readFileSync(path.join(docsDir, filename), "utf8");
      doc = doc.replace(/^---.*---\n/s, ""); // remove front matter
      const docName = doc.substring(2, doc.indexOf("\n"));
      language[docName] = doc;
    });
  // add functions and examples
  const functions = structuredClone(definitions);
  language.functions = [];
  const addFunction = (key: string, func: any, name?: string, varKey?: string, varVal?: any) => {
    const f: any = {
      description: key + (name ?? "") + " - " + func.description,
      notes: func.notes,
      usageNotes: func.usageNotes,
      argumentsNotes: func.argumentsNotes,
    };
    const inputSchema: any = {
      type: "object",
      properties: {
        ["$$" + key]: func.argumentsAsInputSchema ? argumentsAsInputSchema(func) : fixType(func.inputSchema),
      },
      required: ["$$" + key],
    };
    if (func.argumentsAsInputSchema) {
      let minItems = 0,
        maxItems = 0;
      const items: any[] = [];
      func.arguments?.forEach((arg: any) => {
        if (typeof arg.position === "number") {
          maxItems++;
          if (arg.required) {
            minItems++;
          }
          items.push(fixType(arg));
        }
      });
      inputSchema.properties["$$" + key] = {
        type: "array",
        minItems,
        maxItems,
        items,
      };
    } else {
      func.arguments?.forEach((arg: any) => {
        if (arg.required) inputSchema.required.push(arg.name);
        inputSchema.properties[arg.name] = fixType(arg);
      });
    }
    f.schema = inputSchema;
    if (func.outputSchemaTemplate) {
      f.outputSchemaTemplate = fixType(func.outputSchemaTemplate);
    }
    f.examples = examples[key].filter(
      (x: any) => x.given.definition["$$" + key] && (!varKey || x.given.definition[varKey] === varVal),
    );
    language.functions.push(f);
  };
  for (const key in functions) {
    try {
      if (functions[key].subfunctions) {
        let varKey = "",
          varVals = [];
        functions[key].subfunctions.forEach((subFunc: any) => {
          const f = {
            inputSchema: functions[key].inputSchema,
            outputSchema: functions[key].outputSchema,
            ...subFunc.then,
          };
          varKey = subFunc.if[0].argument;
          varVals.push(subFunc.if[0].equals);
          addFunction(
            key,
            f,
            ` (${subFunc.if[0].argument} = ${subFunc.if[0].equals})`,
            subFunc.if[0].argument,
            subFunc.if[0].equals,
          );
        });
        const varArg = functions[key].arguments.find(x => x.name === varKey);
        if (varArg.type === "enum") {
          if (varArg.enum) {
            varArg.enum = varArg.enum.filter((x: any) => !varVals.includes(x));
            if (varArg.enum.length > 0) {
              addFunction(key, functions[key]);
            }
          }
        } else if (varArg.type === "boolean") {
          if (typeof varArg.default === "boolean" && varVals.length === 1 && varArg.default !== varVals[0]) {
            addFunction(key, functions[key]);
          }
        } else {
          addFunction(key, functions[key]);
        }
      } else {
        addFunction(key, functions[key]);
      }
    } catch (e: any) {
      console.error(`Error with func ${key}`, e);
    }
  }
  return language;
}

//const languageFileContents = JSON.stringify(language, null, 2);
//const languageFileName = "language.json";

//fs.writeFileSync(path.join(__dirname, languageFileName), languageFileContents, "utf8");
