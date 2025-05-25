import path from "node:path";
import fs from "node:fs";
import { definitions, examples } from "@nlighten/json-transform-core";

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
  language.functions = structuredClone(definitions);
  for (const key in language.functions) {
    language.functions[key].examples = examples[key];
  }
  return language;
}
