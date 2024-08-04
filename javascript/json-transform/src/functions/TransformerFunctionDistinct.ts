import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import { FunctionDescription } from "./common/FunctionDescription";
import FunctionContext from "./common/FunctionContext";
import { isNullOrUndefined } from "../JsonHelpers";
import JsonElementStreamer from "../JsonElementStreamer";
import stableStringify from "fast-json-stable-stringify";

const DESCRIPTION: FunctionDescription = {
  aliases: ["distinct"],
  inputType: ArgType.Array,
  description: "",
  arguments: {
    by: {
      type: ArgType.Transformer,
      position: 0,
      defaultIsNull: true,
      description:
        "A mapping for each element to distinct by (instead of the whole element, using ##current to refer to the current item)",
    },
  },
  outputType: ArgType.Array,
};
class TransformerFunctionDistinct extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null) return null;

    const by = await context.getJsonElement("by", false);
    const stream = streamer.stream();
    return JsonElementStreamer.fromTransformedStream(
      context,
      isNullOrUndefined(by)
        ? stream.distinctBy(stableStringify)
        : stream.distinctBy(async item => stableStringify(await context.transformItem(by, item))),
    );
  }
}

export default TransformerFunctionDistinct;
