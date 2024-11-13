import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { createComparator, isNullOrUndefined } from "../JsonHelpers";

class TransformerFunctionMax extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        default: { type: ArgType.Object, position: 0 },
        by: { type: ArgType.Transformer, position: 2, defaultString: "##current" },
        type: { type: ArgType.Enum, position: 1, defaultEnum: "AUTO" },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null || streamer.knownAsEmpty()) return null;
    const by = await context.getJsonElement("by", true);

    const type = await context.getEnum("type");

    const def = await context.getJsonElement("default", true);
    const comparator = createComparator(type);
    return streamer
      .stream()
      .map(async t => {
        const res = !isNullOrUndefined(by) ? await context.transformItem(by, t) : t;
        return isNullOrUndefined(res) ? def : res;
      })
      .maxWith(comparator.compare);
  }
}

export default TransformerFunctionMax;
