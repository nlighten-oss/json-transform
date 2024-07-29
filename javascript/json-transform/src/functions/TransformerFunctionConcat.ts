import TransformerFunction from "./common/TransformerFunction";
import {ArgType} from "./common/ArgType";
import {FunctionDescription} from "./common/FunctionDescription";
import FunctionContext from "./common/FunctionContext";
import {isNullOrUndefined, isTruthy} from "../JsonHelpers";
import JsonElementStreamer from "../JsonElementStreamer";
import {asSequence, emptySequence, sequenceOf} from "sequency";

const DESCRIPTION : FunctionDescription = {
  aliases: ["concat"],
  inputType: ArgType.Array,
  description: "",
  outputType: ArgType.Array
};
class TransformerFunctionConcat extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override apply(context: FunctionContext): any {
    const streamer = context.getJsonElementStreamer(null);
    if (streamer == null) return null;

    return JsonElementStreamer.fromTransformedStream(context, streamer.stream()
      .flatMap(itm => {
        if (isNullOrUndefined(itm)) {
          return emptySequence();
        } else if (Array.isArray(itm)) {
          return asSequence(itm);
        }
        return sequenceOf(itm);
      }));
  }
}

export default TransformerFunctionConcat;