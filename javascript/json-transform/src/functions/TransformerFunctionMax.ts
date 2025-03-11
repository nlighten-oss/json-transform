import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { createComparator, isNullOrUndefined } from "../JsonHelpers";
import CompareBy from "./common/CompareBy";

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
    const hasBy = context.has("by");
    const type = await context.getEnum("type");
    const def = await (type === "NUMBER" ? context.getBigDecimal("default") : context.getJsonElement("default"));

    if (!hasBy) {
      const comparator = createComparator(type);
      return streamer
        .stream()
        .map(async t => {
          return isNullOrUndefined(t) ? def : t;
        })
        .maxWith(comparator.compare);
    } else {
      const by = await context.getJsonElement("by", false);
      const comparator = CompareBy.createByComparator(0, type);
      return streamer
        .stream()
        .map(async item => {
          const cb = new CompareBy(item);
          const t = await context.transformItem(by, item);
          cb.by = [isNullOrUndefined(t) ? def : t];
          return cb;
        })
        .maxWith(comparator.compare)
        .then(x => x?.value);
    }
  }
}

export default TransformerFunctionMax;
