import fs from "node:fs";
import path from "node:path";
import { Compilation, Compiler } from "webpack";
import { definitions, examples } from "@nlighten/json-transform-core";

const language: any = { };
// add all markdown files
const docsDir = path.join(__dirname, "..", "docs");
fs.readdirSync(docsDir).filter(f => f.endsWith(".md")).forEach((filename) => {
  let doc = fs.readFileSync(path.join(docsDir, filename), 'utf8');
  doc = doc.replace(/^---.*---\n/s, ""); // remove front matter
  const docName = doc.substring(2, doc.indexOf("\n"));
  language[docName] = doc;
});
// add functions and examples
language.functions = structuredClone(definitions);
for (const key in language.functions) {
  language.functions[key].examples = examples[key];
}
const languageFileContents = JSON.stringify(language);

interface Options {
  targetFilePath: string;
}

const defaultOptions : Options = {
  targetFilePath: 'language.json',
}

export function generateLanguageFile(options: Options = defaultOptions) {
  return new GenerateLanguageFileWebpackPlugin(options);
}

class GenerateLanguageFileWebpackPlugin {
  private readonly name: string;
  private readonly options: Options = {...defaultOptions};

  public constructor(options: Options) {
    this.name = 'GenerateLanguageFileWebpackPlugin';
    this.options = options;
  }

  public apply(compiler: Compiler):void {
    const source = new compiler.webpack.sources.RawSource(languageFileContents);

    compiler.hooks.thisCompilation.tap(this.name, (compilation) => {
      const logger = compilation.getLogger(this.name);

      compilation.hooks.processAssets.tapAsync({
        name: this.name,
        stage: Compilation.PROCESS_ASSETS_STAGE_ADDITIONAL
      }, (unusedAssets, callback) => {
        const filename = this.options.targetFilePath;
        logger.info(`starting to generate language file (${filename})`);
        try {
          const existingAsset = compilation.getAsset(filename);
          if (existingAsset) {
            compilation.updateAsset(filename, source, {languageFile: true});
          } else {
            compilation.emitAsset(filename, source, {languageFile: true});
          }
          logger.info("finished generating language file")
        } catch (e) {
          logger.error(e);
        }
        callback();
      });
    });
  }
}