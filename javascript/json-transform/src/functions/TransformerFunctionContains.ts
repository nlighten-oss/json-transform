import TransformerFunction from "./common/TransformerFunction";
import {ArgType} from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import {FunctionDescription} from "./common/FunctionDescription";
import {isEqual, isNullOrUndefined, isTruthy} from "../JsonHelpers";

const DESCRIPTION : FunctionDescription = {
  aliases: ["contains"],
  description: "",
  inputType: ArgType.Array,
  arguments: {
    that: {
      type: ArgType.Any, position: 0, defaultIsNull: true,
      description: "The value to look for"
    }
  },
  outputType: ArgType.Boolean
};
class TransformerFunctionContains extends TransformerFunction {
  constructor() {
    super(DESCRIPTION);
  }

  override apply(context: FunctionContext): any {
    const streamer = context.getJsonElementStreamer(null);
    if (streamer == null) return null;
    const that = context.getJsonElement( "that");
    return streamer.stream().any(el => isEqual(el, that));
  }
}

export default TransformerFunctionContains;