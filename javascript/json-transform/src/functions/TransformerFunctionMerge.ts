import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { JsonMergeOptions, merge } from "../JsonHelpers";
import { AsyncSequence, asyncSequenceOf } from "@wortise/sequency";

class TransformerFunctionMerge extends TransformerFunction {
  constructor() {
    super({
      arguments: {
        deep: { type: ArgType.Boolean, position: 1, defaultBoolean: false },
        arrays: { type: ArgType.Boolean, position: 2, defaultBoolean: false },
      },
    });
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    if (streamer == null || streamer.knownAsEmpty()) return null;

    const deep = await context.getBoolean("deep");
    const arrays = await context.getBoolean("arrays");

    const options: JsonMergeOptions = {
      deep: deep ?? undefined,
      concatArrays: arrays ?? undefined,
    };

    let result: Record<string, any> = {};

    return (asyncSequenceOf(asyncSequenceOf(result), streamer.stream()).flatten() as AsyncSequence<any>).reduce(
      async (acc, value) => {
        return merge(result, value, options);
      },
    );
  }
}

export default TransformerFunctionMerge;
