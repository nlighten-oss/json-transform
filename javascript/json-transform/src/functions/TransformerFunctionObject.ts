import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { FunctionDescription } from "./common/FunctionDescription";
import { getAsString, isNullOrUndefined } from "../JsonHelpers";

const DESCRIPTION: FunctionDescription = {
  aliases: ["object"],
  description: "",
  inputType: ArgType.ArrayOfArray,
  outputType: ArgType.Object,
};
class TransformerFunctionObject extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
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
