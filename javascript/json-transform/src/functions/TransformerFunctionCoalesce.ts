import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { isNullOrUndefined } from "../JsonHelpers";

class TransformerFunctionCoalesce extends TransformerFunction {
  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null || streamer.knownAsEmpty()) return null;
    return streamer
      .stream()
      .filter(itm => !isNullOrUndefined(itm))
      .firstOrNull();
  }
}

export default TransformerFunctionCoalesce;
