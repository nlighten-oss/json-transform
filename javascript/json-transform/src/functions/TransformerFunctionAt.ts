import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionAt extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        index: { type: ArgType.Integer, position: 0, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const value = await context.getJsonElementStreamer(null);
    if (value == null) {
      return null;
    }
    const index = await context.getInteger("index");
    if (index == null) {
      return null;
    }
    if (index == 0) {
      return value.stream().firstOrNull();
    }
    if (index > 0) {
      return value.stream(index).firstOrNull();
    }
    // negative
    const arr = await value.toJsonArray();
    return arr.at(index) ?? null;
  }
}

export default TransformerFunctionAt;
