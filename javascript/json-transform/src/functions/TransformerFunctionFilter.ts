import { asAsyncSequence, asyncSequenceOf, emptyAsyncSequence } from "@wortise/sequency";
import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import { FunctionDescription } from "./common/FunctionDescription";
import FunctionContext from "./common/FunctionContext";
import { isNullOrUndefined, isTruthy } from "../JsonHelpers";
import JsonElementStreamer from "../JsonElementStreamer";

const DESCRIPTION: FunctionDescription = {
  aliases: ["filter"],
  inputType: ArgType.Array,
  description: "",
  arguments: {
    by: {
      type: ArgType.Transformer,
      position: 0,
      defaultIsNull: true,
      description:
        "A predicate transformer for an element (##current to refer to the current item and ##index to its index)",
    },
  },
  outputType: ArgType.Array,
};
class TransformerFunctionFilter extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null) return null;

    const by = await context.getJsonElement("by", false);
    let index = 0;
    return JsonElementStreamer.fromTransformedStream(
      context,
      streamer.stream().filter(async item => {
        const condition = await context.transformItem(by, item, index++);
        return isTruthy(condition);
      }),
    );
  }
}

export default TransformerFunctionFilter;
