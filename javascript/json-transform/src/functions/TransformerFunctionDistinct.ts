import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { isNullOrUndefined } from "../JsonHelpers";
import JsonElementStreamer from "../JsonElementStreamer";
import stableStringify from "fast-json-stable-stringify";

const getUnique = (value: any) => {
  if (value && typeof value === "object") {
    return stableStringify(value);
  }
  return value;
};

class TransformerFunctionDistinct extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        by: { type: ArgType.Transformer, position: 0, defaultIsNull: true },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null) return null;

    const hasBy = context.has("by");
    const by = await context.getJsonElement("by", false);
    const stream = streamer.stream();
    return JsonElementStreamer.fromTransformedStream(
      context,
      !hasBy
        ? stream.distinctBy(getUnique)
        : stream.distinctBy(async item => getUnique(await context.transformItem(by, item))),
    );
  }
}

export default TransformerFunctionDistinct;
