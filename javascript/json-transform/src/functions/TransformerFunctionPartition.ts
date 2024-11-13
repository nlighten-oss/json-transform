import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";

class TransformerFunctionPartition extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        size: { type: ArgType.Integer, position: 0, defaultInteger: 100 },
      },
    });
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
