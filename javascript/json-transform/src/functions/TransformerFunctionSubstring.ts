import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import JsonElementStreamer from "../JsonElementStreamer";

class TransformerFunctionSubstring extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        begin: { type: ArgType.Integer, position: 0, defaultInteger: 0 },
        end: { type: ArgType.Integer, position: 1, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const str = await context.getString(null);
    if (str == null) {
      return null;
    }
    let beginIndex = await context.getInteger("begin");
    if (beginIndex == null) {
      return str;
    }

    const endIndex = await context.getInteger("end");
    return str.slice(beginIndex, endIndex ?? undefined);
  }
}

export default TransformerFunctionSubstring;
