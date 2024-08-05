import { asAsyncSequence, asyncSequenceOf, emptyAsyncSequence } from "@wortise/sequency";
import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import { FunctionDescription } from "./common/FunctionDescription";
import FunctionContext from "./common/FunctionContext";
import { isNullOrUndefined } from "../JsonHelpers";
import JsonElementStreamer from "../JsonElementStreamer";

const DESCRIPTION: FunctionDescription = {
  aliases: ["flat"],
  inputType: ArgType.Array,
  description: "",
  outputType: ArgType.Array,
};
class TransformerFunctionFlat extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null) return null;

    return JsonElementStreamer.fromTransformedStream(
      context,
      streamer
        .stream()
        .flatMap(itm => {
          if (isNullOrUndefined(itm)) {
            return emptyAsyncSequence();
          } else if (Array.isArray(itm)) {
            return asAsyncSequence(itm);
          }
          return asyncSequenceOf(itm);
        })
        .filter(el => !isNullOrUndefined(el)),
    );
  }
}

export default TransformerFunctionFlat;
