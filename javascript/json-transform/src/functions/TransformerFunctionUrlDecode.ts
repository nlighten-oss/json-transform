import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { ArgType } from "./common/ArgType";

class TransformerFunctionUrlDecode extends TransformerFunction {
  PLUS_REGEX = /\+/g;
  BE_BOM_REGEX = /\\ufeff/gi;
  LE_BOM_REGEX = /\\ufffe/gi;
  CONSECUTIVE_REGEX = /%([0-9a-fA-F][0-9a-fA-F])%([0-9a-fA-F][0-9a-fA-F])/g;

  constructor() {
    super({
      arguments: {
        charset: { type: ArgType.Enum, position: 0, defaultEnum: "UTF-8" },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return null;
    }
    try {
      const charset = await context.getEnum("charset");
      if (charset?.startsWith("UTF-16")) {
        const le = charset === "UTF-16LE";
        return JSON.parse(
          '"' +
            str
              .replace(this.CONSECUTIVE_REGEX, le ? "\\u$2$1" : "\\u$1$2")
              .replace(le ? this.LE_BOM_REGEX : this.BE_BOM_REGEX, "")
              .replace(/"/g, '\\"') +
            '"',
        );
      }
      return decodeURIComponent(str.replace(this.PLUS_REGEX, "%20"));
    } catch (e: any) {
      console.warn(context.getAlias() + " failed", e);
      return null;
    }
  }
}

export default TransformerFunctionUrlDecode;
