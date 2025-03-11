import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { isEqual } from "../JsonHelpers";

class TransformerFunctionIndexOf extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        of: { type: ArgType.Any, position: 0 },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null) return null;

    const of = await context.getJsonElement("of");
    return await streamer.stream().indexOfFirst(async item => {
      return isEqual(of, item);
    });
  }
}

export default TransformerFunctionIndexOf;
