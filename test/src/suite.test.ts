import fs from 'fs';
import path from 'path';
import {describe, test} from "vitest";
import {assertTransformation, JSONBig} from "./assertTransformation";
import {ImplsByLang} from "./implementations";

const language = process.env.IMPL_LANG ?? "java";

const testsFolder = __dirname + "/../tests";
const testsSubfolders = fs.readdirSync(testsFolder);

describe.each(ImplsByLang[language])('%s', implementation => {
  describe.each(testsSubfolders)('%s', folder => {
    const testsFiles = fs.readdirSync(path.join(testsFolder, folder)).map(file => ({file, name: file.slice(0, -5)}));
    describe.each(testsFiles)('$name', ({file}) => {
      const fileTests = JSONBig.parse(fs.readFileSync(path.join(testsFolder, folder, file), 'utf-8'))
        .map((t: any, i: number) => [i + 1, (t.skip ? "[SKIPPED] " : "") + t.name, t]) as [number, string, any]
      test.for(fileTests)("%i. %s", ([i, n, t], ctx) => {
        if (t.skip === true || (Array.isArray(t.skip) && t.skip.includes(implementation))) {
          console.warn("Skipping test: " + t.skip);
          //ctx.skip();
          return;
        }
        return assertTransformation(t, implementation)
      });
    }, {concurrent: true});
  }, {concurrent: true})
});

