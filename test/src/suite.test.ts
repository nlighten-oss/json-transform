import fs from 'fs';
import path from 'path';
import {describe, test} from "vitest";
import {assertTransformation, JSONBig} from "./assertTransformation";
import {ImplsByLang} from "./implementations";
import { examples, JsonTransformExample } from "@nlighten/json-transform-core"
import {EmbeddedTransformerFunction} from "@nlighten/json-transform-core/src";

const language = process.env.IMPL_LANG ?? "javascript";

const testsFolder = __dirname + "/../tests";
const EXAMPLES_FOLDER = "functions";
const testsSubfolders = fs.readdirSync(testsFolder).concat(EXAMPLES_FOLDER);

const getFolderTestFiles: (folder: string) => { file: string, name: string }[] = (folder) => {
  if (folder === EXAMPLES_FOLDER) {
    return Object.keys(examples).map(key => ({file: key, name: key}));
  } else {
    return fs.readdirSync(path.join(testsFolder, folder)).map(file => ({file, name: file.slice(0, -5)}));
  }
}

const getTestsFileContents = (folder: string, file: string) => {
  if (folder === EXAMPLES_FOLDER) {
    return JSONBig.parse(JSONBig.stringify(examples[file as EmbeddedTransformerFunction]));
  } else{
    return JSONBig.parse(fs.readFileSync(path.join(testsFolder, folder, file), 'utf-8'))
  }
}

describe.each(ImplsByLang[language])('%s', implementation => {
  describe.each(testsSubfolders)('%s', folder => {
    describe.each(getFolderTestFiles(folder))('$name', ({ file }) => {
      const fileTests = getTestsFileContents(folder, file)
        .map((t: any, i: number) => [i + 1, (t.skip ? "[SKIPPED] " : "") + t.name, t]) as [number, string, any]
      test.for(fileTests)("%i. %s", ([i, n, t], ctx) => {
        if (t.skip === true || (Array.isArray(t.skip) && t.skip.includes(implementation))) {
          console.warn("Skipping test: " + t.skip);
          //ctx.skip();
          return;
        }
        return assertTransformation(t as JsonTransformExample, implementation)
      });
    }, {concurrent: true});
  }, {concurrent: true})
});

