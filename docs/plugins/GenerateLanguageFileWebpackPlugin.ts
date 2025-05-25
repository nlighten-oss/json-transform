import { Compilation, Compiler } from "webpack";
import languageFile from "./generators/languageFile";

interface Options {
  targetFilePath: string;
  generator: () => any;
}

const defaultOptions: Options = {
  targetFilePath: "language.json",
  generator: languageFile,
};

export function generateLanguageFile(options: Options = defaultOptions) {
  return new GenerateLanguageFileWebpackPlugin(options);
}

class GenerateLanguageFileWebpackPlugin {
  private readonly name: string;
  private readonly options: Options = { ...defaultOptions };

  public constructor(options: Options) {
    this.name = "GenerateLanguageFileWebpackPlugin";
    this.options = options;
  }

  public apply(compiler: Compiler): void {
    const source = new compiler.webpack.sources.RawSource(JSON.stringify(this.options.generator()));

    compiler.hooks.thisCompilation.tap(this.name, compilation => {
      const logger = compilation.getLogger(this.name);

      compilation.hooks.processAssets.tapAsync(
        {
          name: this.name,
          stage: Compilation.PROCESS_ASSETS_STAGE_ADDITIONAL,
        },
        (unusedAssets, callback) => {
          const filename = this.options.targetFilePath;
          logger.info(`starting to generate language file (${filename})`);
          try {
            const existingAsset = compilation.getAsset(filename);
            if (existingAsset) {
              compilation.updateAsset(filename, source, { languageFile: true });
            } else {
              compilation.emitAsset(filename, source, { languageFile: true });
            }
            logger.info("finished generating language file");
          } catch (e) {
            logger.error(e);
          }
          callback();
        },
      );
    });
  }
}
