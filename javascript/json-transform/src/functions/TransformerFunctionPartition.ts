import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";

const DESCRIPTION: FunctionDescription = {
  aliases: ["partition"],
  description: "",
  inputType: ArgType.Array,
  arguments: {
    size: {
      type: ArgType.Integer,
      position: 0,
      defaultInteger: 100,
      required: true,
      description: "The size of each partition",
    },
  },
  outputType: ArgType.ArrayOfArray,
};
class TransformerFunctionPartition extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override async apply(context: FunctionContext): Promise<any> {
    const stream = await context.getJsonElementStreamer(null);
    if (stream == null) {
      return null;
    }
    const size = await context.getInteger("size");
    return stream.stream().chunk(size ?? 100);
  }
}

export default TransformerFunctionPartition;
