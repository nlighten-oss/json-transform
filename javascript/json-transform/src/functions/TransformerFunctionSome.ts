import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { isTruthy } from "../JsonHelpers";

class TransformerFunctionSome extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        by: { type: ArgType.Transformer, position: 0 },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null) return false;

    const by = await context.getJsonElement("by", false); // we don't transform definitions to prevent premature evaluation
    return streamer
      .stream()
      .map(x => context.transformItem(by, x))
      .any(item => isTruthy(item));
  }
}

export default TransformerFunctionSome;
