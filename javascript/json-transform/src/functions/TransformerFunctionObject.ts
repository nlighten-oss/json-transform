import TransformerFunction from "./common/TransformerFunction";
import FunctionContext from "./common/FunctionContext";
import { getAsString, isNullOrUndefined } from "../JsonHelpers";

class TransformerFunctionObject extends TransformerFunction {
  constructor() {
    super({});
  }

  override async apply(context: FunctionContext): Promise<any> {
    const streamer = await context.getJsonElementStreamer(null);
    const result: Record<string, any> = {};
    if (streamer != null) {
      await streamer.stream().forEach(entry => {
        if (Array.isArray(entry)) {
          const size = entry.length;
          if (size > 1) {
            const key = entry[0];
            if (!isNullOrUndefined(key)) {
              result[getAsString(key)] = entry[1];
            }
          }
        }
      });
    }
    return result;
  }
}

export default TransformerFunctionObject;
