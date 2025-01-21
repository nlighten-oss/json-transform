import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionPad extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        direction: { type: ArgType.Enum, position: 0, defaultIsNull: true },
        width: { type: ArgType.Integer, position: 1, defaultIsNull: true },
        pad_string: { type: ArgType.String, position: 2, defaultString: "0" },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const res = await context.getString(null);
    if (res == null) return null;

    const direction = await context.getEnum("direction");
    const width = await context.getInteger("width");
    if (direction == null || width == null || res.length >= width) {
      return res;
    }

    const paddingSize = width - res.length;
    let padding = ((await context.getString("pad_string")) ?? "").repeat(paddingSize);
    if (padding.length > paddingSize) {
      // in case padding string is more than one character
      padding = padding.substring(0, paddingSize);
    }
    if ("LEFT" === direction || "START" === direction) {
      return padding + res;
    } else if ("RIGHT" === direction || "END" === direction) {
      return res + padding;
    }
  }
}

export default TransformerFunctionPad;
