import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import JsonElementStreamer from "../JsonElementStreamer";

class TransformerFunctionReverse extends TransformerFunction {
  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null) {
      return null;
    }
    return JsonElementStreamer.fromTransformedStream(context, streamer.stream().reverse());
  }
}

export default TransformerFunctionReverse;
