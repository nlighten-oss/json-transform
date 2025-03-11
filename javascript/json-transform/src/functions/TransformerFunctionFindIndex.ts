import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { isTruthy } from "../JsonHelpers";

class TransformerFunctionFindIndex extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        by: { type: ArgType.Transformer, position: 0, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null) return null;

    const hasBy = context.has("by");
    const by = await context.getJsonElement("by", false);
    let index = 0;
    return await streamer.stream().indexOfFirst(async item => {
      if (!hasBy) {
        return isTruthy(item);
      }
      const condition = await context.transformItem(by, item, index++);
      return isTruthy(condition);
    });
  }
}

export default TransformerFunctionFindIndex;
