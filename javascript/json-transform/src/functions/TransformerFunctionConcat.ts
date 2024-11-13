import { asAsyncSequence, asyncSequenceOf, emptyAsyncSequence } from "@wortise/sequency";
import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { isNullOrUndefined } from "../JsonHelpers";
import JsonElementStreamer from "../JsonElementStreamer";

class TransformerFunctionConcat extends TransformerFunction {
  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null) return null;

    return JsonElementStreamer.fromTransformedStream(
      context,
      streamer.stream().flatMap(itm => {
        if (isNullOrUndefined(itm)) {
          return emptyAsyncSequence();
        } else if (Array.isArray(itm)) {
          return asAsyncSequence(itm);
        }
        return asyncSequenceOf(itm);
      }),
    );
  }
}

export default TransformerFunctionConcat;
