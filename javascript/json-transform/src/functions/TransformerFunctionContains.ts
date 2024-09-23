import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { isEqual } from "../JsonHelpers";

class TransformerFunctionContains extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        that: { type: ArgType.Any, position: 0, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null) return null;
    const that = await context.getJsonElement("that");
    return streamer.stream().any(el => isEqual(el, that));
  }
}

export default TransformerFunctionContains;
