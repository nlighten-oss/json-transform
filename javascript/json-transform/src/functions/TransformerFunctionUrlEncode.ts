import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { ArgType } from "./common/ArgType";

class TransformerFunctionUrlEncode extends TransformerFunction {
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
        throw new Error("TODO: not implemented"); // TODO: not implemented
      }
      return encodeURIComponent(str).replace(/%20/g, "+");
    } catch (e: any) {
      console.warn(context.getAlias() + " failed", e);
      return null;
    }
  }
}

export default TransformerFunctionUrlEncode;
