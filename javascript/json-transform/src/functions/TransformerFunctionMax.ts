import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";
import { createComparator, isNullOrUndefined } from "../JsonHelpers";

const DESCRIPTION: FunctionDescription = {
  aliases: ["max"],
  description: "",
  inputType: ArgType.Array,
  arguments: {
    default: { type: ArgType.Object, position: 0, description: "The default value to use for empty values" },
    by: {
      type: ArgType.Transformer,
      position: 2,
      defaultString: "##current",
      description: "A transformer to extract a property to max by (using ##current to refer to the current item)",
    },
    type: {
      type: ArgType.Enum,
      position: 1,
      defaultEnum: "AUTO",
      enumValues: ["AUTO", "STRING", "NUMBER", "BOOLEAN"],
      description: "Type of values to expect when ordering the input array",
    },
  },
  outputType: ArgType.BigDecimal,
};
class TransformerFunctionMax extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
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
